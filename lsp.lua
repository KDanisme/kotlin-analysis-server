vim.lsp.start({
	name = "kotlin",
	cmd = { "./build/install/kotlin-analysis-server/bin/kotlin-analysis-server" },
	root_dir = vim.fs.root(0, { "settings.gradle.kts" }),
})
