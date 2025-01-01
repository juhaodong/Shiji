package modules.network


object IkResponseConvertor {
    private fun <T> errorHandler(response: IKResponse<T>): IKResponse<T> {
        return response
    }

    fun <T> fetchFromResponse(response: IKResponse<T>): T {
        val ikResponse = errorHandler(response)
        if (ikResponse.status == "good") {
            return ikResponse.content
        } else {
            throw Exception(ikResponse.info)
        }
    }

    fun <T> fetchFromResponse(response: T): T {
        if (response != null) {
            return response
        } else {
            throw Exception("Error")
        }
    }
}


