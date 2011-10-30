class FeedListener {
    private FeedCache cache

    FeedListener(FeedCache cache) {
        this.cache = cache
    }

    def notifyFeedEntry(FeedEntry entry) {
        println "processing feed entry ${entry.pageLink}"
        cache.addFeedEntry(entry)
    }
}
