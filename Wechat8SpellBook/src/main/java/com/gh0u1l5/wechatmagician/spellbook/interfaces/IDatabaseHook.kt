package com.gh0u1l5.wechatmagician.spellbook.interfaces

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.nop

interface IDatabaseHook {

    /**
     * Called when Wechat is going to invoke [SQLiteDatabase.openDatabase] method.
     *
     * @param path to database file to open and/or create.
     * @param factory an optional factory class that is called to instantiate a cursor when query is called.
     * @param flags to control database access mode.
     * @param errorHandler the optional SQLiteErrorHandler object to be used to handle corruption.
     * @return to bypass the original method, return a WCDB SQLiteDatabase object wrapped by
     * [Operation.replacement], or a throwable wrapped by [Operation.interruption], otherwise return
     * [Operation.nop].
     */
    fun onDatabaseOpening(path: String, factory: Any?, flags: Int, errorHandler: Any?): Operation<Any> = nop()

    /**
     * Called when Wechat has finished calling [SQLiteDatabase.openDatabase] method.
     *
     * @param path to database file to open and/or create.
     * @param factory an optional factory class that is called to instantiate a cursor when query is called.
     * @param flags to control database access mode.
     * @param errorHandler the optional SQLiteErrorHandler object to be used to handle corruption.
     * @param result the database object generated by [SQLiteDatabase.openDatabase] method.
     * @return to replace the original result, return a WCDB SQLiteDatabase object wrapped by
     * [Operation.replacement], otherwise return [Operation.nop].
     */
    fun onDatabaseOpened(path: String, factory: Any?, flags: Int, errorHandler: Any?, result: Any?): Operation<Any> = nop()

    /**
     * Called when a WCDB SQLiteDatabase object is going to invoke [SQLiteDatabase.rawQueryWithFactory] method.
     *
     * @param factory the cursor factory to use, or null for the default factory.
     * @param sql the SQL query. The SQL string must not be ; terminated.
     * @param selectionArgs one may include ?s in where clause in the query, which will be replaced
     * by the values from selectionArgs.
     * @param editTable the name of the first table, which is editable.
     * @param cancellationSignal a signal to cancel the operation in progress, or null if none.
     * @return to bypass the original method, return [Operation.interruption] or a WCDB Cursor object
     * wrapped by [Operation.replacement], otherwise return [Operation.nop].
     */
    fun onDatabaseQuerying(thisObject: Any, factory: Any?, sql: String, selectionArgs: Array<Any>?, editTable: String?, cancellationSignal: Any?): Operation<Any> = nop()

    /**
     * Called when a WCDB SQLiteDatabase object has returned from [SQLiteDatabase.rawQueryWithFactory] method.
     *
     * @param factory the cursor factory to use, or null for the default factory.
     * @param sql the SQL query. The SQL string must not be ; terminated.
     * @param selectionArgs one may include ?s in where clause in the query, which will be replaced
     * by the values from selectionArgs.
     * @param editTable the name of the first table, which is editable.
     * @param cancellationSignal a signal to cancel the operation in progress, or null if none.
     * @param result the cursor object generated by [SQLiteDatabase.rawQueryWithFactory] method.
     * @return to replace the original result, return a WCDB Cursor object wrapped by
     * [Operation.replacement], otherwise return [Operation.nop].
     */
    fun onDatabaseQueried(thisObject: Any, factory: Any?, sql: String, selectionArgs: Array<Any>?, editTable: String?, cancellationSignal: Any?, result: Any?): Operation<Any> = nop()

    /**
     * Called when a WCDB SQLiteDatabase object is going to invoke [SQLiteDatabase.insertWithOnConflict] method.
     *
     * @param table the table to insert the row into.
     * @param nullColumnHack optional, see [SQLiteDatabase.insertWithOnConflict].
     * @param initialValues the initial column values for the row. The keys should be the column
     * names and the values the column values.
     * @param conflictAlgorithm for insert conflict resolver.
     * @return to bypass the original method, return a Long number wrapped by [Operation.replacement],
     * otherwise return [Operation.nop]. The Long number represents the row ID of the newly inserted
     * row OR -1 if no row is inserted.
     */
    fun onDatabaseInserting(thisObject: Any, table: String, nullColumnHack: String?, initialValues: ContentValues?, conflictAlgorithm: Int): Operation<Long> = nop()

    /**
     * Called when a WCDB SQLiteDatabase object has returned from [SQLiteDatabase.insertWithOnConflict] method.
     *
     * @param table the table to insert the row into.
     * @param nullColumnHack optional, see [SQLiteDatabase.insertWithOnConflict].
     * @param initialValues the initial column values for the row. The keys should be the column
     * names and the values the column values.
     * @param conflictAlgorithm for insert conflict resolver.
     * @param result the row ID of the newly inserted row OR -1 if either the input parameter
     * conflictAlgorithm = CONFLICT_IGNORE or an error occurred.
     * @return to replace the original result, return a Long number wrapped by [Operation.replacement],
     * otherwise return [Operation.nop].
     */
    fun onDatabaseInserted(thisObject: Any, table: String, nullColumnHack: String?, initialValues: ContentValues?, conflictAlgorithm: Int, result: Long?): Operation<Long> = nop()

    /**
     * Called when a WCDB SQLiteDatabase object is going to invoke [SQLiteDatabase.updateWithOnConflict] method.
     *
     * @param table the table to update in
     * @param values a map from column names to new column values. null is a valid value that will
     * be translated to NULL.
     * @param whereClause the optional WHERE clause to apply when updating. Passing null will update all rows.
     * @param whereArgs You may include ?s in the where clause, which will be replaced by the values from whereArgs.
     * @param conflictAlgorithm for update conflict resolver
     * @return to bypass the original method, return a Int number wrapped by [Operation.replacement],
     * otherwise return [Operation.nop]. The Int number represents the number of rows affected.
     */
    fun onDatabaseUpdating(thisObject: Any, table: String, values: ContentValues, whereClause: String?, whereArgs: Array<String>?, conflictAlgorithm: Int): Operation<Int> = nop()

    /**
     * Called when a WCDB SQLiteDatabase object has returned from [SQLiteDatabase.updateWithOnConflict] method.
     *
     * @param table the table to update in
     * @param values a map from column names to new column values. null is a valid value that will
     * be translated to NULL.
     * @param whereClause the optional WHERE clause to apply when updating. Passing null will update all rows.
     * @param whereArgs You may include ?s in the where clause, which will be replaced by the values from whereArgs.
     * @param conflictAlgorithm for update conflict resolver
     * @param result the number of rows affected
     * @return to replace the original result, return a Int number wrapped by [Operation.replacement],
     * otherwise return [Operation.nop].
     */
    fun onDatabaseUpdated(thisObject: Any, table: String, values: ContentValues, whereClause: String?, whereArgs: Array<String>?, conflictAlgorithm: Int, result: Int): Operation<Int> = nop()

    /**
     * Called when a WCDB SQLiteDatabase object is going to invoke [SQLiteDatabase.delete] method.
     *
     * @param table the table to delete from
     * @param whereClause the optional WHERE clause to apply when deleting. Passing null will delete all rows.
     * @param whereArgs You may include ?s in the where clause, which will be replaced by the values from whereArgs.
     * @return to bypass the original method, return a Int number wrapped by [Operation.replacement],
     * otherwise return [Operation.nop]. The Int number represents the number of rows affected if a
     * whereClause is passed in, 0 otherwise.
     */
    fun onDatabaseDeleting(thisObject: Any, table: String, whereClause: String?, whereArgs: Array<String>?): Operation<Int> = nop()

    /**
     * Called when a WCDB SQLiteDatabase object has returned from [SQLiteDatabase.delete] method.
     *
     * @param table the table to delete from
     * @param whereClause the optional WHERE clause to apply when deleting. Passing null will delete all rows.
     * @param whereArgs You may include ?s in the where clause, which will be replaced by the values from whereArgs.
     * @param result the number of rows affected if a whereClause is passed in, 0 otherwise.
     * @return to replace the original result, return a Int number wrapped by [Operation.replacement],
     * otherwise return [Operation.nop].
     */
    fun onDatabaseDeleted(thisObject: Any, table: String, whereClause: String?, whereArgs: Array<String>?, result: Int): Operation<Int> = nop()

    /**
     * Called when a WCDB SQLiteDatabase object is going to invoke [SQLiteDatabase.execSQL] method.
     *
     * @param sql the SQL statement to be executed.
     * @param bindArgs only byte[], String, Long and Double are supported in bindArgs.
     * @param cancellationSignal a signal to cancel the operation in progress, or null if none.
     * @return to bypass the original method, return `true`, otherwise return `false`.
     */
    fun onDatabaseExecuting(thisObject: Any, sql: String, bindArgs: Array<Any?>?, cancellationSignal: Any?) = false

    /**
     * Called when a WCDB SQLiteDatabase object has returned from [SQLiteDatabase.execSQL] method.
     *
     * @param sql the SQL statement to be executed.
     * @param bindArgs only byte[], String, Long and Double are supported in bindArgs.
     * @param cancellationSignal a signal to cancel the operation in progress, or null if none.
     */
    fun onDatabaseExecuted(thisObject: Any, sql: String, bindArgs: Array<Any?>?, cancellationSignal: Any?) { }
}