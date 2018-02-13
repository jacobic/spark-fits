package com.sparkfits

import org.scalatest.{BeforeAndAfterAll, FunSuite, FlatSpec, Matchers}
import org.scalatest.Matchers._

import org.apache.spark.sql.SparkSession

import com.sparkfits.fits._

/**
  *Test class for the package object.
  */
class ReadFitsTest extends FunSuite with BeforeAndAfterAll {

  private val master = "local[2]"
  private val appName = "sparkfitsTest"

  private var spark : SparkSession = _

  override protected def beforeAll() : Unit = {
    super.beforeAll()
    spark = SparkSession
      .builder()
      .master(master)
      .appName(appName)
      .getOrCreate()
  }

  override protected def afterAll(): Unit = {
    try {
      spark.sparkContext.stop()
    } finally {
      super.afterAll()
    }
  }
  // END TODO

  // Add more and put a loop for several tests!
  val fn = "src/test/resources/test.fits"

  // Test if the user provides the data type in the HDU
  test("dataType test: is there table or image in options?") {
    val results = spark.readfits
    val exception = intercept[NoSuchElementException] {
      results.load(fn)
    }
    assert(exception.getMessage.contains("datatype"))
  }

  // Test if the data type is table (image not yet supported)
  test("dataType test: Is the datatype a table?") {
    val results = spark.readfits
    val exception = intercept[AssertionError] {
      results.option("datatype", "image").load(fn)
    }
    assert(exception.getMessage.contains("datatype"))
  }

  // Test if the user provides the HDU index to be read
  test("HDU test: Is there a HDU number?") {
    val results = spark.readfits
    val exception = intercept[NoSuchElementException] {
      results.option("datatype", "table").load(fn)
    }
    assert(exception.getMessage.contains("HDU"))
  }

  // Test if the user provides the HDU index to be read
  test("HDU test: Is HDU index above the max HDU index?") {
    val results = spark.readfits
    val exception = intercept[AssertionError] {
      results.option("datatype", "table").option("HDU", 30).load(fn)
    }
    assert(exception.getMessage.contains("HDU"))
  }

  // Test if the user provides the data type in the HDU
  test("Table test: Are you really accessing a Table?") {
    val results = spark.readfits
    val exception = intercept[ClassCastException] {
      results.option("datatype", "table").option("HDU", 0).load(fn)
    }
    assert(exception.getMessage.contains("BinaryTableHDU"))
  }

  // Test if one accesses column as expected for HDU 1
  test("Count test 1: Do you count all elements in a column?") {
    val results = spark.readfits
      .option("datatype", "table")
      .option("HDU", 1)
      .load(fn)
    assert(results.select("RA").count() == 100)
  }

  // Test if one accesses column as expected for HDU 1
  test("Count test 2: Do you count all elements in a column?") {
    val results = spark.readfits
      .option("datatype", "table")
      .option("HDU", 2)
      .load(fn)
    assert(results.select("Index").count() == 100)
  }

}
