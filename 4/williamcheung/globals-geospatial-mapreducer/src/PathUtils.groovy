class PathUtils {

    static def path(parentSubscripts, currentSubscript) {
        def pathSubscripts = []
        pathSubscripts.addAll(parentSubscripts)
        pathSubscripts.add(currentSubscript)
        pathSubscripts.toArray()
    }
}
