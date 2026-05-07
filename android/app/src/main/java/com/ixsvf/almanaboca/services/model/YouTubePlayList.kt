package com.ixsvf.almanaboca.services.model

import com.google.firebase.firestore.PropertyName

// --- MODELO BLINDADO PARA O YOUTUBE ---

data class YouTubeResponse(
    val items: List<PlaylistItem>? = emptyList()
)

data class PlaylistItem(
    val snippet: Snippet? = null // Nullable para evitar crash se o item vier vazio
)

data class Snippet(
    val title: String? = "",
    val description: String? = "",
    val thumbnails: Thumbnails? = null,
    val resourceId: ResourceId? = null
)

data class Thumbnails(
    val medium: ThumbnailUrl? = null,
    val high: ThumbnailUrl? = null, // Adicionei o High para teres melhor qualidade se disponível

    // Usamos crases porque 'default' é uma palavra reservada no Kotlin
    @get:PropertyName("default")
    @set:PropertyName("default")
    var default: ThumbnailUrl? = null
)

data class ThumbnailUrl(
    val url: String? = ""
)

data class ResourceId(
    val videoId: String? = ""
)