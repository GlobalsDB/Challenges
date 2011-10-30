import groovy.swing.SwingBuilder

import javax.swing.JFrame
import javax.swing.JTextArea

feedCache = new FeedCache()
feedLoader = new FeedLoader(new FeedListener(feedCache))

swing = new SwingBuilder()
frame = swing.frame(
    title: 'Groovy Globals',
    show: true,
    size: [800, 600],
    locationRelativeTo: null,
    defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE) {
    menuBar {
        menu('Flickr feed') {
            menuItem(text: 'Load', actionPerformed: {
                feedLoader.loadFeed()
            })
        }
    }
    panel {
        tableLayout {
            FeedEntry.propNames().each { prop ->
                tr {
                    td {label prop}
                    td {textField(id: prop, columns: 20)}
                }
            }
            tr {
                td(colspan: 2, align: 'RIGHT') {button(text: 'search', actionPerformed: {
                    search()
                })}
            }
        }
        scrollPane {
            textArea(id: 'results', rows: 15, columns: 60)
        }
    }
}

def search() {
    def prototype = buildPrototypeFeedEntry()
    def entries = feedCache.searchFeedEntries(prototype)

    JTextArea results = swing.results
    results.text = ''

    entries.each { entry ->
        entry.retrieveProps().each { prop ->
            results.append(prop.key)
            results.append(':\t')
            results.append(prop.value)
            results.append('\n')
        }
        results.append('============================================================\n')
    }

    results.caretPosition = 0
}

def buildPrototypeFeedEntry() {
   def entry = new FeedEntry()
   FeedEntry.propNames().each { prop ->
       entry."${prop}" = swing."${prop}".text
   }

   entry
}