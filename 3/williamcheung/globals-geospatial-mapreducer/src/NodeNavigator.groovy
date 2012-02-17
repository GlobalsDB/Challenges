import com.intersys.globals.NodeReference

class NodeNavigator {

    private def globalName
    private def global
    private def listener

    def ignoreLeafNode

    NodeNavigator(global, listener) {
        if (global instanceof NodeReference)
            this.global = global
        else
            this.globalName = global
        this.listener = listener
    }

    def navigateNodes() {
        def connection

        try {
            if (!global) {
                connection = new ConnectionFactory().createConnection(listener)
                global = connection.createNodeReference(globalName)
            }

            navigate()
        } catch (e) {
            listener.notifyException(e)
        } finally {
            if (connection) { // we allocated resources, so we must release them
                if (global)
                    global.close()
                connection.close()
            }
        }
    }

    private def navigate(parentSubscripts = [].toArray()) {
        def currentSubscript = ''
        def eof = false
        while (!eof) {
            currentSubscript = global.nextSubscript(path(parentSubscripts, currentSubscript))

            eof = currentSubscript.empty
            if (!eof)
                navigate(path(parentSubscripts, currentSubscript))
            else {
                def data = global.getObject(parentSubscripts)
                if (data) { // hit leaf node (only node with data) - end of recursion
                    def subscriptsToUse = ignoreLeafNode ?
                        parentSubscripts[0..parentSubscripts.length-2] : parentSubscripts

                    listener.notifyLeafNode(subscriptsToUse, data)
                }
            }
        }
    }

    private def path(parentSubscripts, currentSubscript) {
        def pathSubscripts = []
        pathSubscripts.addAll(parentSubscripts)
        pathSubscripts.add(currentSubscript)
        pathSubscripts.toArray()
    }
}
