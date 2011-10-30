class FeedLoader {
    static private URL = 'http://api.flickr.com/services/feeds/photos_public.gne'

    private FeedListener listener

    FeedLoader(FeedListener listener) {
        this.listener = listener
    }

    def loadFeed() {
        def entries = new XmlParser().parse(URL).entry
        for (entry in entries) {
            def model = new FeedEntry(
                title: entry.title.text(),
                pageLink: entry.link.find{it.@rel == 'alternate'}.@href,
                id: entry.id.text(),
                published: entry.published.text(),
                updated: entry.updated.text(),
                dateTaken: entry['dc:date.Taken'].text(),
                content: entry.content.text(),
                authorName: entry.author.name.text(),
                authorUri: entry.author.uri.text(),
                authorNsid: entry.author['flickr:nsid'].text(),
                authorBuddyIcon: entry.author['flickr:buddyicon'].text(),
                imageLink: entry.link.find{it.@rel == 'enclosure'}.@href
            )

            listener.notifyFeedEntry(model)
        }
    }
}
