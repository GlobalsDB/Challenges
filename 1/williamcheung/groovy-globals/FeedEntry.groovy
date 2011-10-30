class FeedEntry {
    def title // ex: IMG_6699
    def pageLink // ex: http://www.flickr.com/photos/lfcastro/6294470533/
    def id // ex: tag:flickr.com,2005:/photo/6294470533
    def published // ex: 2011-10-30T14:25:08Z
    def updated // ex: 2011-10-30T14:25:08Z
    def dateTaken // ex: 2011-10-23T15:14:12-08:00
    def content // ex: html
    def authorName // ex: Luiz Felipe Castro
    def authorUri // ex: http://www.flickr.com/people/lfcastro/
    def authorNsid // ex: 31818496@N00
    def authorBuddyIcon // ex: http://farm4.static.flickr.com/3095/buddyicons/31818496@N00.jpg?1254970484#31818496@N00
    def imageLink // ex: http://farm7.static.flickr.com/6217/6294470533_1e5bf1ec3b_b.jpg

    Map<String, Object> retrieveProps() {
        properties.findAll() {it.key != 'class' && it.key != 'metaClass'}.sort()
    }

    static List<String> propNames() {
        def names = []
        new FeedEntry().retrieveProps().each { prop ->
            names << prop.key
        }
        names
    }

    boolean determineIfEmpty() {
        for (prop in retrieveProps()) {
            if (prop.value)
                return false
        }
        true
    }
}
