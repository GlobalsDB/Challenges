import com.intersys.globals.NodeReference

class NodeNavigator {

    private def globalName
    private def global
    private def listener

    def ignoreLeafNode
    def maxNodeDepth = -1

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
        def stop = false
        while (!stop) {
            currentSubscript = global.nextSubscript(PathUtils.path(parentSubscripts, currentSubscript))
            def currentSubscriptPath = PathUtils.path(parentSubscripts, currentSubscript)

            stop = currentSubscript.empty || currentSubscriptPath.length == maxNodeDepth
            if (!stop)
                navigate(currentSubscriptPath)
            else {
                def data = global.getObject(parentSubscripts)
                if (data) { // hit leaf node (only node with data) - end of recursion
                    def subscriptsToUse = ignoreLeafNode ?
                        parentSubscripts[0..parentSubscripts.length-2] : parentSubscripts

                    listener.notifyLeafNode(subscriptsToUse, data)
                } else if (currentSubscriptPath.length == maxNodeDepth) {
                    listener.notifyMaxNodeDepthReached(currentSubscriptPath)
                }
            }
        }
    }
}
