import com.intersys.globals.Connection
import com.intersys.globals.ConnectionContext
import com.intersys.globals.NodeReference
import com.intersys.globals.ValueList

class FeedCache {

    private Connection connection =  ConnectionContext.connection
    private NodeReference global

    FeedCache() {
        if (!connection.isConnected()) {
            connection.connect()
            println 'connected to Globals db'
        }
        global = connection.createNodeReference('uploads')
    }

    def addFeedEntry(FeedEntry entry) {
        def list = connection.createList()
        entry.retrieveProps().each { prop ->
            println prop
            list.append(prop.key)
            list.append(prop.value)
        }
        global.set(list, entry.id)
        searchFeedEntries('', '')
    }

    List<FeedEntry> searchFeedEntries(FeedEntry prototype) {
        def entries = []
        def returnAll = prototype.determineIfEmpty()

        def currentSubscript = ''
        def eof = false
        while (!eof) {
            currentSubscript = global.nextSubscript(currentSubscript)
            eof = currentSubscript.empty
            if (!eof) {
                def list = global.getList(currentSubscript)
                def entry = rebuildFeedEntry(list)
                if (returnAll || entryMatchesPrototype(entry, prototype))
                    entries << entry
                println entry.id
            }
        }

        entries
    }

    private FeedEntry rebuildFeedEntry(ValueList list) {
        def entry = new FeedEntry()

        def numProps = list.length() / 2
        for (i in 1..numProps) {
            def name = list.getNextString()
            def value = list.getNextString()
            entry."${name}" = value
        }

        entry
    }

    private boolean entryMatchesPrototype(entry, prototype) {
        for (protoProp in prototype.retrieveProps()) {
            def key = protoProp.key

            def protoValue = protoProp.value.toLowerCase()
            def entryValue = entry."${key}".toLowerCase()

            if (protoValue && !entryValue.startsWith(protoValue))
                return false
        }

        true
    }
}
