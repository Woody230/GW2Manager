# Need to keep class names because the type table stores based on name.
-keepnames class ** { *; }

# Needed for the System.loadLibrary("kodein-leveldb-jni") call.
-keep class org.kodein.db.** { native <methods>; }