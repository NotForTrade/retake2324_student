package com.example.retake2324_student


import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

//const val SQL_host = "10.1.1.105"
const val SQL_host = "10.0.2.2" // NB: docker container, "magic" IP used for the emulator to access localhost
const val SQL_port = 3306

const val SQL_url = "jdbc:mysql://$SQL_host:$SQL_port/test"
// NB: not compatible w/ android
//const val SQL_driver = "com.mysql.cj.jdbc.Driver"
const val SQL_driver = "com.mysql.jdbc.Driver"

//const val SQL_url = "jdbc:mariadb://$SQL_host:$SQL_port/test?useSSL=false"
//const val SQL_driver = "org.mariadb.jdbc.Driver"

//const val SQL_user = "remote"
//const val SQL_passwd = "MysqlRemote2024.:!"
const val SQL_user = "root"
const val SQL_passwd = "root"

fun connectDatabase(): Database {
    return Database.connect(
        SQL_url,
        driver = SQL_driver,
        user = SQL_user,
        password = SQL_passwd,
        logger = ConsoleLogger(threshold = LogLevel.INFO)
    ).also {
        println("Database connection established successfully.")
    }
}

fun isDbAccessible(): Boolean {
    return isPortAccessible(SQL_host, SQL_port)
}

fun isPortAccessible(host: String, port: Int, timeout: Int = 2000): Boolean {
    return try {
        Socket().use { socket ->
            socket.connect(InetSocketAddress(host, port), timeout)
            true
        }
    } catch (e: IOException) {
        false
    }
}