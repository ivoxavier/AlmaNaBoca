package com.ixsvf.almanaboca.screens.menusubscreens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YouTubeScreen(videoId: String) {
    val context = LocalContext.current

    // 1. Manter o ecrã ligado (Cinema Mode)
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        factory = { ctx ->
            YouTubePlayerView(ctx).apply {
                // Importante na v13: Gerir o ciclo de vida
                (ctx as? LifecycleOwner)?.lifecycle?.addObserver(this)

                // Desligamos a auto-inicialização para configurar opções personalizadas
                enableAutomaticInitialization = false

                val listener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        // Agora que estamos na v13, pode tentar 'loadVideo' (autoplay)
                        // Se falhar, volte para 'cueVideo'.
                        youTubePlayer.loadVideo(videoId, 0f)
                    }

                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                        super.onError(youTubePlayer, error)
                        // Agora só deve ver erros reais, não bugs de sistema
                        android.util.Log.e("ALMANABOCA_PLAYER", "Erro: $error")
                    }
                }

                // Configurações visuais do player
                val options = IFramePlayerOptions.Builder(ctx)
                    .controls(1)
                    .rel(0) // Não mostrar vídeos relacionados de outros canais
                    .fullscreen(0) // Gerimos o fullscreen com o Compose se necessário
                    .build()

                initialize(listener, options)
            }
        }
    )
}