package com.ixsvf.almanaboca.services.model

data class YouTubeResponse(val items: List<PlaylistItem>? = emptyList() )
data class PlaylistItem(val snippet: Snippet)
data class Snippet(
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val resourceId: ResourceId
)
data class Thumbnails(val medium: ThumbnailUrl)
data class ThumbnailUrl(val url: String)
data class ResourceId(val videoId: String)