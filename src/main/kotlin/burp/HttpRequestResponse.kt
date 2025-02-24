package burp

import java.io.Serializable

data class HttpRequestResponse(
    override var request: ByteArray, override var response: ByteArray?, override var comment: String?,
    override var highlight: String?, override var httpService: IHttpService
): IHttpRequestResponse, Serializable {

    companion object{
        fun fromHttpRequestResponse(rr: IHttpRequestResponse): HttpRequestResponse{
            return HttpRequestResponse(rr.request, rr.response, rr.comment, rr.highlight, HttpService.fromHttpService(rr.httpService))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HttpRequestResponse

        if (!request.contentEquals(other.request)) return false
        if (response != null) {
            if (other.response == null) return false
            if (!response.contentEquals(other.response)) return false
        } else if (other.response != null) return false
        if (comment != other.comment) return false
        if (highlight != other.highlight) return false
        if (httpService != other.httpService) return false

        return true
    }

    override fun hashCode(): Int {
        var result = request.contentHashCode()
        result = 31 * result + (response?.contentHashCode() ?: 0)
        result = 31 * result + (comment?.hashCode() ?: 0)
        result = 31 * result + (highlight?.hashCode() ?: 0)
        result = 31 * result + httpService.hashCode()
        return result
    }

}