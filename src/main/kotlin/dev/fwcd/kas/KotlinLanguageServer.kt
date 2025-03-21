package dev.fwcd.kas

import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.*
import org.jetbrains.kotlin.analysis.api.KaImplementationDetail
import org.jetbrains.kotlin.analysis.api.platform.lifetime.KotlinAlwaysAccessibleLifetimeTokenFactory
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import java.net.URI
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger
import kotlin.jvm.java

private val LOG = Logger.getLogger(KotlinLanguageServer::class.java.name)

/**
 * The language server implementation, responsible for basic lifecycle management, i.e.
 * initialization and shutdown. The request implementations are handled by
 * `KotlinTextDocumentService` and `KotlinWorkspaceService`.
 */
class KotlinLanguageServer :
    LanguageServer,
    LanguageClientAware {
    /** The text document service responsible for handling code completion requests, etc. */
    private val textDocuments = KotlinTextDocumentService()

    /** The text document service responsible for handling workspace updates, etc. */
    private val workspaces = KotlinWorkspaceService()

    /** A proxy object for sending messages to the client. */
    private var client: LanguageClient? = null

    @OptIn(KaImplementationDetail::class)
    override fun initialize(params: InitializeParams): CompletableFuture<InitializeResult> {
        // TODO: Investigate proper lifecycle management with disposables (should we store a Disposable in the class?)
        // TODO: Add a proper logging abstraction that uses LSP's logMessage underneath

        client?.logMessage(MessageParams(MessageType.Info, "Locating sources..."))
        // TODO: Make source-resolution more flexible (currently only Gradle-style src/main/kotlin folders are considered)
        val workspaceFolders = params.workspaceFolders ?: listOf()
        val sourceRoots =
            workspaceFolders
                .map {
                    Path
                        .of(URI(it.uri))
                        .resolve("src")
                        .resolve("main")
                        .resolve("kotlin")
                }

        // Configure headless IDEA to not spawn an app in the Dock
        // https://stackoverflow.com/questions/17460777/stop-java-coffee-cup-icon-from-appearing-in-the-dock-on-mac-osx
        System.setProperty("apple.awt.UIElement", "true")

        client?.logMessage(MessageParams(MessageType.Info, "Setting up standalone analysis API session..."))

        textDocuments.session =
            buildStandaloneAnalysisAPISession {
                registerProjectService(
                    KotlinAlwaysAccessibleLifetimeTokenFactory::class.java,
                )
                buildKtModuleProvider {
                    platform = TargetMiP
                    addModule(
                        buildKtSourceModule {
                            moduleName = "Language server project sources" // TODO
                            platform = JvmPlatforms.defaultJvmPlatform

                            // TODO: We should handle (virtual) file changes announced via LSP with the VFS
                            addSourceRoots(sourceRoots)
                        },
                    )
                }
            }

        return CompletableFuture.completedFuture(
            InitializeResult(
                ServerCapabilities().apply {
                    completionProvider = CompletionOptions()
                    diagnosticProvider = DiagnosticRegistrationOptions()
                    renameProvider = Either.forLeft(true)
                },
                ServerInfo("Kotlin Analysis Server"),
            ),
        )
    }

    override fun connect(client: LanguageClient) {
        this.client = client
    }

    override fun shutdown(): CompletableFuture<Any> = CompletableFuture.completedFuture(Unit)

    override fun exit() {}

    override fun getTextDocumentService(): TextDocumentService = textDocuments

    override fun getWorkspaceService(): WorkspaceService = workspaces
}
