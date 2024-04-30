package com.app.salesforce.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.salesforce.R
import com.app.salesforce.network.RestService
import com.app.salesforce.offline.AppDataBase
import com.app.salesforce.offline.Areas
import com.app.salesforce.offline.Cities
import com.app.salesforce.offline.Regions
import com.app.salesforce.offline.Schools
import com.app.salesforce.response.CustomersRelatedtoSO
import com.app.salesforce.response.GetServerResponse
import com.app.salesforce.response.IdName
import com.app.salesforce.response.Region
import com.app.salesforce.utilities.AppKeys
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SyncDataActivity : AppCompatActivity() {

    // TODO: 0. Have backend response fixed for Cities and Schools when response is empty.
    // TODO: 1. Clean code. Remove all the logs.
    // TODO: 2. UI implementation. Show loading when data is being downloaded and saved into DB

    private lateinit var regionList: MutableList<Region>
    private lateinit var citiesList: MutableList<IdName>
    private lateinit var areasList: MutableList<IdName>
    private lateinit var schoolsList: MutableList<CustomersRelatedtoSO>

    private val restService by lazy { RestService.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync_data)
        Log.d("class_name", this.javaClass.simpleName)
        Log.d("SOID", "SOID: ${Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE).Data.SOID}")
    }

    fun downloadsdData(view: View) {
        deleteOldData()
        downloadRegions()
        downloadCities(0)
    }

    private fun deleteOldData() {

        //region Initializing/removing any present data
        regionList = mutableListOf()
        citiesList = mutableListOf()
        areasList = mutableListOf()
        schoolsList = mutableListOf()
        //endregion

        Thread(Runnable {

            AppDataBase.getInstance(this)
                    .regionDao()
                    .deleteRegions()
//            Log.d("ehtie", "Regions Deleted")

            AppDataBase.getInstance(this)
                    .cityDao()
                    .deleteCities()
//            Log.d("ehtie", "Cities Deleted")

            AppDataBase.getInstance(this)
                    .areaDao()
                    .deleteAreas()
//            Log.d("ehtie", "Areas Deleted")

            AppDataBase.getInstance(this)
                    .schoolDao()
                    .deleteSchools()
//            Log.d("ehtie", "Schools Deleted")

        }).start()
    }

    private fun downloadRegions() {
        for (region in Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE).Data.Region) {

            if (!regionList.contains(region)) {
                regionList.add(region)
            }
        }
    }

    private fun downloadCities(index: Int) {

        if (index == regionList.size) {
            Log.d("ehtie", "Number of cities: $index")
            downloadAreas(0)
            return
        }

        val disposable: Disposable = restService.getCities(regionList[index].ID.toString(),
                Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE).Data.SOID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> addCitiesToList(result, regionList[index].ID, index) },
                        { error -> showError("Unable get download cities", error.message) }
                )
    }

    private fun addCitiesToList(result: GetServerResponse, regionID: Int, index: Int) {

        for (value in result.Cities) {
            if (!citiesList.contains(value)) {
                value.parentId = regionID
                citiesList.add(value)
            }
        }
        downloadCities(index + 1)
    }

    private fun downloadAreas(index: Int) {

        if (index == citiesList.size) {
            Log.d("ehtie", "Number of Areas: $index")
            downloadSchools(0)
            return
        }

        val disposable: Disposable = restService.getAreas(citiesList[index].ID.toString(),
                Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE).Data.SOID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> addAreasToList(result, citiesList[index].ID, index) },
                        { error -> showError("Unable to download Areas", error.message) }
                )
    }

    private fun addAreasToList(result: GetServerResponse, cityID: Int, index: Int) {

        for (value in result.Areas) {

            if (!areasList.contains(value) && value.ID != 0) {
                value.parentId = cityID
                areasList.add(value)
            }
        }
        downloadAreas(index + 1)
    }

    private fun downloadSchools(index: Int) {

        if (index == areasList.size) {
            Log.d("ehtie", "Number of schools: $index")
            saveData()
            return
        }

        Log.d("ehtie", "CityID: " + areasList[index].parentId + "AreaID: " + areasList[index].ID)
        val disposable: Disposable = restService.getSchools(areasList[index].parentId, areasList[index].ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> addSchoolsToList(result, areasList[index].ID, areasList[index].parentId, index) },
                        { error -> showError("Unable to download schools", error.message) }
                )

    }

    private fun addSchoolsToList(result: GetServerResponse, areaID: Int, cityID: Int, index: Int) {

        for (value in result.CustomersRelatedtoSO!!) {
            if (value.ID != 0) {
                value.AreaID = areaID
                value.CityID = cityID
                schoolsList.add(value)
            }
        }
        downloadSchools(index + 1)
    }

    private fun showError(unable: String, message: String?) {
        Toast.makeText(this, "$unable $message", Toast.LENGTH_SHORT).show()
    }

    private fun saveData() {

        //region Remove these counts along with logs
        var regionCount = 0
        var citiesCount = 0
        var areaCount = 0
        var schoolCount = 0
        //endregion

        for (region in regionList) {
            Thread(Runnable {
                AppDataBase.getInstance(this)
                        .regionDao()
                        .insertRegionInfo(Regions(region.ID, region.Name))
//                Log.d("ehtie", "regionCount: $regionCount")
                regionCount++
            }).start()
        }

        for (city in citiesList) {
            Thread(Runnable {
                AppDataBase.getInstance(this)
                        .cityDao()
                        .insertCityInfo(Cities(city.ID, city.Name, city.parentId))
//                Log.d("ehtie", "citiesCount: $citiesCount")
                citiesCount++
            }).start()
        }

        for (area in areasList) {
            Thread(Runnable {
                AppDataBase.getInstance(this)
                        .areaDao()
                        .insertAreaInfo(Areas(area.ID, area.Name, area.parentId))
//                Log.d("ehtie", "areaCount: $areaCount")
                areaCount++
            }).start()
        }

        for (school in schoolsList) {
            Thread(Runnable {
                AppDataBase.getInstance(this)
                        .schoolDao()
                        .insertSchoolInfo(Schools(school.ID, school.ShopName, school.AreaID, school.CityID))
//                Log.d("ehtie", "schoolCount: $schoolCount")
                schoolCount++
            }).start()
        }
    }

    // Remove function after implementation
    fun testResultLogs(view: View) {

        Thread(Runnable {

            Log.d("ehtie", "Getting regions")

            val regions = AppDataBase.getInstance(this)
                    .regionDao()
                    .allRegions

            for (region in regions) {
                Log.d("ehtie", "List of regions: $region")
            }

            Log.d("ehtie", "Getting cities")

            val cities = AppDataBase.getInstance(this)
                    .cityDao()
                    .allCities

            for (city in cities) {
                Log.d("ehtie", "List of city: $city")
            }

            Log.d("ehtie", "Getting areas")

            val areas = AppDataBase.getInstance(this)
                    .areaDao()
                    .allAreas

            for (area in areas) {
                Log.d("ehtie", "List of area: $area")
            }

            Log.d("ehtie", "Getting schools")

            val schools = AppDataBase.getInstance(this)
                    .schoolDao()
                    .allSchools

            for (school in schools) {
                Log.d("ehtie", "List of school: $school")
            }
        }).start()

        //region Data stored in List
        Log.d("ehtie", "regions: $regionList")
//
//        Log.d("ehtie", "cities: $citiesList")
//
        for (i in citiesList) {
            Log.d("ehtie", "cityName: ${i.Name}")
            Log.d("ehtie", "regionID: ${i.parentId}")
        }
//
        Log.d("ehtie", "areas: $areasList")
//
        for (i in areasList) {
            Log.d("ehtie", "areaName: ${i.Name}")
            Log.d("ehtie", "cityID: ${i.parentId}")
        }
//
        for (i in schoolsList) {
            Log.d("ehtie", "Schools Name: ${i.ShopName} \tAreaID: ${i.AreaID} \tCityID: ${i.CityID} ")
        }
        Log.d("ehtie", "Number of Schools: ${schoolsList.size}")
        //endregion

    }
}
