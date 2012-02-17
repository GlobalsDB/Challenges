class Exporter {

    private def globalName
    private def file
    private def printWriter
    private def statusListener

    def ignoreLeafNode

    Exporter(globalName, file, listener) {
        this.globalName = globalName
        this.file = file
        this.statusListener = listener
    }

    def exportGlobalToCSV() {
        try {
            printWriter = file.newPrintWriter()

            def navigator = new NodeNavigator(globalName, this)
            navigator.ignoreLeafNode = ignoreLeafNode

            statusListener.notifyStatus('exporter started')

            navigator.navigateNodes()

            statusListener.notifyStatus("exporter complete - see file: ${file.absolutePath}")
        } catch (e) {
            statusListener.notifyException(e)
        } finally {
            printWriter.close()
        }
    }

    // listener methods, invoked by NodeNavigator

    def notifyLeafNode(subscripts, data) {
        def line = new StringBuffer()
        line << subscripts.join(',')
        line << ','
        line << data
        printWriter.println(line)
    }

    def notifyStatus(message) {
        statusListener.notifyStatus(message)
    }

    def notifyException(e) {
        statusListener.notifyException(e)
    }
}
