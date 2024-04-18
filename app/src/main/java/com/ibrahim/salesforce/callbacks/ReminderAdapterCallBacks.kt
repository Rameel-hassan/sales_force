package com.ibrahim.salesforce.callbacks

import com.ibrahim.salesforce.response.Reminder

interface ReminderAdapterCallBacks {


    fun  deleteReminder(item: Reminder,position:Int ,Reminder : String)
    fun rescheduleReminder(item: Reminder,position:Int, ReminderDate:String)

}