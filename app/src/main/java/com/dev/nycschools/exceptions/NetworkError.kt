package com.dev.nycschools.exceptions

/**
 * Class for holding network processing error.
 */
class NetworkError(errorDetail: String) : Exception(errorDetail)