package com.example.data.signaling

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

sealed class SignalingMessage {
    data class SdpOffer(val callId: String, val senderId: String, val sdp: String) : SignalingMessage()
    data class SdpAnswer(val callId: String, val senderId: String, val sdp: String) : SignalingMessage()
    data class IceCandidate(val callId: String, val candidate: String, val sdpMid: String, val sdpMLineIndex: Int) : SignalingMessage()
}

data class CallParticipant(
    val id: String,
    val name: String,
    val avatar: String,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val isVideoOff: Boolean = false
)

class SignalingService private constructor() {
    private val _iceCandidateCount = MutableStateFlow(0)
    val iceCandidateCount: StateFlow<Int> = _iceCandidateCount.asStateFlow()

    private val _connectionStatus = MutableStateFlow("P2P WebRTC Connected")
    val connectionStatus: StateFlow<String> = _connectionStatus.asStateFlow()

    companion object {
        @Volatile
        private var instance: SignalingService? = null

        fun getInstance(): SignalingService {
            return instance ?: synchronized(this) {
                instance ?: SignalingService().also { instance = it }
            }
        }
    }

    fun generateLocalSdp(isInitiator: Boolean): String {
        return "v=0\r\no=chatboxpro ${System.currentTimeMillis()} 2 IN IP4 0.0.0.0\r\ns=-\r\nt=0 0\r\na=group:BUNDLE 0 1\r\na=msid-semantic: WMS\r\nm=audio 9 UDP/TLS/RTP/SAVPF 111 103 104 9 0 8 106 105 13 110 112 113 126\r\nc=IN IP4 0.0.0.0\r\na=rtcp:9 IN IP4 0.0.0.0\r\na=ice-ufrag:${UUID.randomUUID().toString().take(6)}\r\na=ice-pwd:${UUID.randomUUID().toString().take(12)}\r\na=ice-options:trickle\r\na=fingerprint:sha-256 4A:AD:B9:7B:64:5E:21:44:E2:0F:85:92:02:44:03:99:A2:7A:B4:7A:00:88\r\na=setup:${if (isInitiator) "actpass" else "active"}"
    }

    fun generateIceCandidates(): List<String> {
        val candidates = listOf(
            "candidate:842163049 1 udp 1686052607 192.168.1.100 52341 typ host generation 0",
            "candidate:281940291 1 udp 2122260223 74.125.190.127 19302 typ srflx raddr 192.168.1.100 rport 52341 generation 0",
            "candidate:992019283 1 udp 1686052607 10.0.2.16 52342 typ host generation 0"
        )
        _iceCandidateCount.value += candidates.size
        return candidates
    }
}
