package com.app.salesforce.callbacks

import com.app.salesforce.response.Reminder

interface ReminderAdapterCallBacks {


    fun  deleteReminder(item: Reminder,position:Int ,Reminder : String)
    fun rescheduleReminder(item: Reminder,position:Int, ReminderDate:String)

}