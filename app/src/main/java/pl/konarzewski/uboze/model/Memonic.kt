package pl.konarzewski.uboze.model

import android.content.Context
import info.debatty.java.stringsimilarity.*
import info.debatty.java.stringsimilarity.experimental.Sift4
import java.io.*

//Pseudo words
//https://www.frontiersin.org/articles/10.3389/fpsyg.2015.01395/full
//https://github.com/ksopyla/awesome-nlp-polish

/*
@Throws(IOException::class)
fun translateText(projectId: String?, targetLanguage: String?, text: String?) {

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    TranslationServiceClient.create().use { client ->
        // Supported Locations: `global`, [glossary location], or [model location]
        // Glossaries must be hosted in `us-central1`
        // Custom Models must use the same location as your model. (us-central1)
        val parent: LocationName = LocationName.of(projectId, "global")

        // Supported Mime Types: https://cloud.google.com/translate/docs/supported-formats
        val request: TranslateTextRequest = TranslateTextRequest.newBuilder()
            .setParent(parent.toString())
            .setMimeType("text/plain")
            .setTargetLanguageCode(targetLanguage)
            .addContents(text)
            .build()
        val response: TranslateTextResponse = client.translateText(request)

        // Display the translation for each input text provided
        for (translation in response.translationsList) {
            System.out.printf("Translated text: %s\n", translation.translatedText)
        }
    }
}
 */

fun getDictPC(path: String, split: String): List<String> {
    val dics = ArrayList<String>()
    File(path).forEachLine { dics.add(it.split(split)[0])}
    return dics
}

fun compress(fileName: String, dump: List<String>) {
    try {
        val fos = FileOutputStream(fileName)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(dump)
        oos.close()
        fos.close()
    } catch (ioe: IOException) {
        ioe.printStackTrace()
    }
}

fun getDict(fileName: String, ctx: Context): List<String> {
    var dics = ArrayList<String>()

    try {
        val ois = ObjectInputStream(ctx.assets.open(fileName))
        dics = ois.readObject() as ArrayList<String>
        ois.close()
    } catch (ioe: IOException) {
        ioe.printStackTrace()
    } catch (c: ClassNotFoundException) {
        println("Class not found")
        c.printStackTrace()
    }

    return dics
}

fun getMemonicWords(word: String, dics: List<String>, comp: (String, String) -> Double): List<String> =
    dics
        .map { it to comp(it, word) }
        .sortedByDescending { (_, value) -> value }
        .take(5).map { it.first }
        .filter { word != it }
        .take(5)

fun getMemonicWordsRewerse(word: String, dics: List<String>, comp: (String, String) -> Double): List<String> =
    dics
        .map { it to comp(it, word) }
        .sortedBy { (_, value) -> value }
        .take(20).map { it.first }
        .filter { word != it }
        .take(5)

fun getMemonicWords1(word: String, dicts: List<String>): String =
    "OptimalStringAlignment " + getMemonicWordsRewerse(word, dicts, OptimalStringAlignment()::distance) + "\n" +
    "NormalizedLevenshtein " + getMemonicWords(word, dicts, NormalizedLevenshtein()::similarity) + "\n" +
    "Sift4 " + getMemonicWordsRewerse(word, dicts, Sift4()::distance) + "\n" +
    "Damerau " + getMemonicWordsRewerse(word, dicts, Damerau()::distance) + "\n" +
    "Levenshtein " + getMemonicWordsRewerse(word, dicts, Levenshtein()::distance) + "\n" +
    "RatcliffObershelp " + getMemonicWords(word, dicts, RatcliffObershelp()::similarity) + "\n" +
    "SorensenDice " + getMemonicWords(word, dicts, SorensenDice()::similarity) + "\n" +
    "CosineDistance " + getMemonicWords(word, dicts, Cosine()::similarity) + "\n" +
    "JaccardDistance " + getMemonicWords(word, dicts, Jaccard()::similarity) + "\n" +
    "JaroWinkler " + getMemonicWords(word, dicts, JaroWinkler()::similarity) + "\n" +
    "Sift4 " + getMemonicWordsRewerse(word, dicts, Sift4()::distance) + "\n" +
    "QGram " + getMemonicWordsRewerse(word, dicts, QGram()::distance)
