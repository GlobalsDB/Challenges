class MapReduceJob extends AbstractRunner {

    private def connection
    private def mapperGlobal
    private def reducerGlobal

    def run() {
        try {
            setupGlobals()

            run new Mapper(mapperGlobal)

            run new Reducer(mapperGlobal, reducerGlobal)

        } catch (e) {
            listener.notifyException(e)
        } finally {
            closeGlobals()
        }
    }

    private def setupGlobals() {
        connection = new ConnectionFactory().createConnection(listener)
        mapperGlobal  = createGlobal(params.mapperGlobal)
        reducerGlobal = createGlobal(params.reducerGlobal)
    }

    private def createGlobal(name) {
        def global = connection.createNodeReference(name)
        global.kill()
        listener.notifyStatus("created global ${name}")
        global
    }

    private def run(runner) {
        runner.params = params
        runner.listener = listener
        runner.run()
    }

    private def closeGlobals() {
        if (mapperGlobal)
            mapperGlobal.close()
        if (reducerGlobal)
            reducerGlobal.close()
        if (connection)
            connection.close()
    }
}
