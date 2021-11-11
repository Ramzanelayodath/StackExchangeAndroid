package com.example.stackexchange.Utils

class Urls {

 var  baseUrl = "https://api.stackexchange.com"

 fun getQuestions(page: String): String? {
  return "$baseUrl/2.3/questions?page=$page&pagesize=15&order=desc&sort=activity&site=stackoverflow"
 }

 fun getAnswers(qId: String, page: String): String? {
  return "$baseUrl/2.3/questions/$qId/answers?page=$page&pagesize=15&order=desc&sort=activity&site=stackoverflow&filter=!6VvPDzQHbd2UL"
 }
}