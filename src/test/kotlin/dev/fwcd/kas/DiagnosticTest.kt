import dev.fwcd.kas.KotlinLanguageServer
import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.DocumentDiagnosticParams
import org.eclipse.lsp4j.DocumentSymbolParams
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.jetbrains.kotlin.wasm.ir.source.location.SourceLocation.IgnoredLocation.file
import kotlin.test.Test

class DiagnosticTest {
    @Test fun `report diagnostics on open`() {

        val server = KotlinLanguageServer().apply {  initialize(InitializeParams())}
        server.textDocumentService.diagnostic(DocumentDiagnosticParams().apply {
            textDocument= TextDocumentIdentifier("src/main/kotlin/dev/fwcd/kas/Main.kt")
        })
    }


}