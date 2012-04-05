interface Listener {
    def notifyStatus(message)
    def notifyException(e)

    def notifyLeafNode(subscripts, data)

    def notifyMaxNodeDepthReached(subscripts)
}
