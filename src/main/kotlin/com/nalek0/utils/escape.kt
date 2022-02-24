package com.nalek0.utils

fun escape(text: String?): String? {
    if (text == null) { return null }
    return text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("\'", "&#039;")
}