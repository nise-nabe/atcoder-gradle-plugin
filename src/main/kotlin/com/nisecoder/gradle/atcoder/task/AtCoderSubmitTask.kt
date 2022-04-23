package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.AtCoderBuildService
import com.nisecoder.gradle.atcoder.internal.AtCoderException
import com.nisecoder.gradle.atcoder.internal.AtCoderLanguage
import com.nisecoder.gradle.atcoder.internal.AtCoderNoSuchTaskException
import com.nisecoder.gradle.atcoder.internal.AtCoderSite
import com.nisecoder.gradle.atcoder.internal.ContestTask
import com.nisecoder.gradle.atcoder.internal.cookieValue
import com.nisecoder.gradle.atcoder.internal.csrfToken
import com.nisecoder.gradle.atcoder.internal.readFirstLine
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import kotlinx.coroutines.runBlocking
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction

abstract class AtCoderSubmitTask: AtCoderTask() {
    @get:Input
    abstract val contestName: Property<String>

    @get:Input
    abstract val taskId: Property<String>

    @get:Input
    abstract val submitLanguage: Property<AtCoderLanguage>

    @get:InputFile
    abstract val taskListFile: RegularFileProperty

    @get:Internal
    abstract val atCoderService: Property<AtCoderBuildService>

    @TaskAction
    fun submit() {
        val session = atCoderService.get().login()

        val task = taskListFile.get().asFile.readLines().map(ContestTask::fromTsvRow)
            .firstOrNull { it.taskId == taskId.get() }
            ?: throw AtCoderNoSuchTaskException("taskId=${taskId.get()} is not found")

        val sourceSets: SourceSetContainer = project.extensions.getByType(SourceSetContainer::class.java)

        val submitFile = sourceSets.getAt(task.taskId).allSource.find { it.name == "main.kt" }
            ?: throw AtCoderException("cannot find file for submit")

        val sourceCode = submitFile.readText()

        runBlocking {
            val client = HttpClient(CIO) {
                expectSuccess = false
                followRedirects = false
            }
            client.submitForm<HttpResponse>(
                url = "${AtCoderSite.contest}/${contestName.get()}/submit",
                formParameters = Parameters.build {
                    append("data.TaskScreenName", task.taskScreenName)
                    append("data.LanguageId", submitLanguage.get().code)
                    append("sourceCode", sourceCode)
                    append("csrf_token", session.csrfToken())
                },
                encodeInQuery = false
            ) {
                header(HttpHeaders.AcceptLanguage, "ja")
                header(HttpHeaders.Cookie, session.cookieValue())
            }
        }
    }
}
