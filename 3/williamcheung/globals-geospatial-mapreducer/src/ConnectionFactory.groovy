import com.intersys.globals.ConnectionContext

class ConnectionFactory {

    def createConnection(listener) {
        def connection = ConnectionContext.connection
        if (!connection.connected) {
            connection.connect()
            listener.notifyStatus('connected to Globals db')
        }

        connection
    }
}
