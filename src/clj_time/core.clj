(ns clj-time.core
  "The core namespace for date-time operations in the clj-time library.

   Create a DateTime instance with date-time, specifying the year, month, day,
   hour, minute, second, and millisecond:

     => (date-time 1986 10 14 4 3 27 456)
     #<DateTime 1986-10-14T04:03:27.456Z>

   Less-significant fields can be omitted:

     => (date-time 1986 10 14)
     #<DateTime 1986-10-14T00:00:00.000Z>

   Get the current time with (now) and the start of the Unix epoch with (epoch).

   Once you have a date-time, use accessors like hour and sec to access the
   corresponding fields:

     => (hour (date-time 1986 10 14 22))
     22

   The date-time constructor always returns times in the UTC time zone. If you
   want a time with the specified fields in a different time zone, use
   from-time-zone:
   
     => (from-time-zone (date-time 1986 10 22) (time-zone-for-offset -2))
     #<DateTime 1986-10-22T00:00:00.000-02:00>
   
   If on the other hand you want a given absolute instant in time in a
   different time zone, use to-time-zone:
   
     => (to-time-zone (date-time 1986 10 22) (time-zone-for-offset -2))
     #<DateTime 1986-10-21T22:00:00.000-02:00>

   In addition to time-zone-for-offset, you can use the time-zone-for-id and
   default-time-zone functions and the utc Var to constgruct or get DateTimeZone
   instances.

   The functions after? and before? determine the relative position of two
   DateTime instances:

     => (after? (date-time 1986 10) (date-time 1986 9))
     true

   Often you will want to find a date some amount of time from a given date. For
   example, to find the time 1 month and 3 weeks from a given date-time:

     => (plus (date-time 1986 10 14) (months 1) (weeks 3))
     #<DateTime 1986-12-05T00:00:00.000Z>

   An Interval is used to represent the span of time between two DateTime
   instances. Construct one using interval, then query them using within?,
   overlaps?, and abuts?

     => (within? (interval (date-time 1986) (date-time 1990))
                 (date-time 1987))
     true
   
   To find the amount of time encompased by an interval, use in-secs and
   in-minutes:
   
     => (in-minutes (interval (date-time 1986 10 2) (date-time 1986 10 14)))
     17280

   Note that all functions in this namespace work with Joda objects or ints. If
   you need to print or parse date-times, see clj-time.format. If you need to
   ceorce date-times to or from other types, see clj-time.coerce."
  (:refer-clojure :exclude [extend])
  (:import (org.joda.time ReadableDateTime DateTime DateMidnight DateTimeZone Period PeriodType Interval Days)))

(def ^{:doc "DateTimeZone for UTC."}
      utc
  (DateTimeZone/UTC))

(defn now []
  "Returns a DateTime for the current instant in the UTC time zone."
  (DateTime. #^DateTimeZone utc))

(defn today-at-midnight []
  "Returns a DateMidnight for today at midnight in the UTC time zone."
  (DateMidnight. #^DateTimeZone utc))

(defn epoch []
  "Returns a DateTime for the begining of the Unix epoch in the UTC time zone."
  (DateTime. (long 0) #^DateTimeZone utc))

(defn date-midnight
  "Constructs and returns a new DateMidnight in UTC.
   Specify the year, month of year, day of month. Note that month and day are
   1-indexed. Any number of least-significant components can be ommited, in which case
   they will default to 1."
  ([year]
    (date-midnight year 1 1))
  ([year month]
    (date-midnight year month 1))
  ([year month day]
    (DateMidnight. year month day #^DateTimeZone utc)))

(defn date-time
  "Constructs and returns a new DateTime in UTC.
   Specify the year, month of year, day of month, hour of day, minute if hour,
   second of minute, and millisecond of second. Note that month and day are
   1-indexed while hour, second, minute, and millis are 0-indexed.
   Any number of least-significant components can be ommited, in which case
   they will default to 1 or 0 as appropriate."
  ([year]
   (date-time year 1 1 0 0 0 0))
  ([year month]
   (date-time year month 1 0 0 0 0))
  ([year month day]
   (date-time year month day 0 0 0 0))
  ([year month day hour]
   (date-time year month day hour 0 0 0))
  ([year month day hour minute]
   (date-time year month day hour minute 0 0))
  ([year month day hour minute second]
   (date-time year month day hour minute second 0))
  ([#^Integer year #^Integer month #^Integer day #^Integer hour
    #^Integer minute #^Integer second #^Integer millis]
   (DateTime. year month day hour minute second millis #^DateTimeZone utc)))

(defn year
  "Return the year component of the given ReadableDateTime."
  [#^ReadableDateTime dt]
  (.getYear dt))

(defn month
  "Return the month-of-year component of the given ReadableDateTime. January is 1."
  [#^ReadableDateTime dt]
  (.getMonthOfYear dt))

(defn day
  "Return the day of month component of the given ReadableDateTime."
  [#^ReadableDateTime dt]
  (.getDayOfMonth dt))

(defn day-of-week
  "Return the day of week component of the given ReadableDateTime. Monday is 1 and
   Sunday is 7."
  [#^ReadableDateTime dt]
  (.getDayOfWeek dt))

(defn hour
  "Return the hour of day component of the given ReadableDateTime. A time of 12:01am
   will have an hour component of 0."
  [#^ReadableDateTime dt]
  (.getHourOfDay dt))

(defn minute
  "Return the minute of hour component of the given ReadableDateTime."
  [#^ReadableDateTime dt]
  (.getMinuteOfHour dt))

(defn sec
  "Return the second-of-minute component of the given ReadableDateTime."
  [#^ReadableDateTime dt]
  (.getSecondOfMinute dt))

(defn milli
  "Return the millisecond-of-second component of the given ReadableDateTime."
  [#^ReadableDateTime dt]
  (.getMillisOfSecond dt))

(defn time-zone-for-offset
  "Returns a DateTimeZone for the given offset, specified either in hours or
   hours and minutes."
  ([hours]
   (DateTimeZone/forOffsetHours hours))
  ([hours minutes]
   (DateTimeZone/forOffsetHoursMinutes hours minutes)))

(defn time-zone-for-id [#^String id]
  "Returns a DateTimeZone for the given ID, which must be in long form, e.g.
   'America/Matamoros'."
  (DateTimeZone/forID id))

(defn default-time-zone []
  "Returns the default DateTimeZone for the current environment."
  (DateTimeZone/getDefault))

(defn to-time-zone
  "Returns a new ReadableDateTime corresponding to the same absolute instant in time as
   the given ReadableDateTime, but with calendar fields corresponding to the given
   TimeZone."
  [#^ReadableDateTime dt #^DateTimeZone tz]
  (.withZone dt tz))

(defn from-time-zone
  "Returns a new ReadableDateTime corresponding to the same point in calendar time as
   the given ReadableDateTime, but for a correspondingly different absolute instant in
   time."
  [#^ReadableDateTime dt #^DateTimeZone tz]
  (.withZoneRetainFields dt tz))

(defn after?
  "Returns true if ReadableDateTime dt-a is strictly after ReadableDateTime dt-b."
  [#^ReadableDateTime dt-a #^ReadableDateTime dt-b]
  (.isAfter dt-a dt-b))

(defn before?
  "Returns true if ReadableDateTime dt-a is strictly before ReadableDateTime dt-b."
  [#^ReadableDateTime dt-a #^ReadableDateTime dt-b]
  (.isBefore dt-a dt-b))

(defn years
  "Given a number, returns a Period representing that many years.
   Without an argument, returns a PeriodType representing only years."
  ([]
     (PeriodType/years))
  ([#^Integer n]
     (Period/years n)))

(defn months
  "Given a number, returns a Period representing that many months.
   Without an argument, returns a PeriodType representing only months."
  ([]
     (PeriodType/months))
  ([#^Integer n]
     (Period/months n)))

(defn weeks
  "Given a number, returns a Period representing that many weeks.
   Without an argument, returns a PeriodType representing only weeks."
  ([]
     (PeriodType/weeks))
  ([#^Integer n]
     (Period/weeks n)))

(defn days
  "Given a number, returns a Period representing that many days.
   Without an argument, returns a PeriodType representing only days."
  ([]
     (PeriodType/days))
  ([#^Integer n]
     (Period/days n)))

(defn hours
  "Given a number, returns a Period representing that many hours.
   Without an argument, returns a PeriodType representing only hours."
  ([]
     (PeriodType/hours))
  ([#^Integer n]
     (Period/hours n)))

(defn minutes
  "Given a number, returns a Period representing that many minutes.
   Without an argument, returns a PeriodType representing only minutes."
  ([]
     (PeriodType/minutes))
  ([#^Integer n]
     (Period/minutes n)))

(defn secs
  "Given a number, returns a Period representing that many seconds.
   Without an argument, returns a PeriodType representing only seconds."
  ([]
     (PeriodType/seconds))
  ([#^Integer n]
     (Period/seconds n)))

(defn millis
  "Given a number, returns a Period representing that many milliseconds.
   Without an argument, returns a PeriodType representing only milliseconds."
  ([]
     (PeriodType/millis))
  ([#^Integer n]
     (Period/millis n)))

(defn plus
  "Returns a new ReadableDateTime corresponding to the given ReadableDateTime moved forwards by
   the given Period(s)."
  ([#^ReadableDateTime dt #^Period p]
   (.plus dt p))
  ([dt p & ps]
   (reduce #(plus %1 %2) (plus dt p) ps)))

(defn minus
  "Returns a new ReadableDateTime corresponding to the given ReadableDateTime moved backwards by
   the given Period(s)."
  ([#^ReadableDateTime dt #^Period p]
   (.minus dt p))
  ([dt p & ps]
   (reduce #(minus %1 %2) (minus dt p) ps)))

(defn ago
  "Returns a DateTime a supplied period before the present.
  e.g. (-> 5 years ago)"
  [#^Period period]
  (minus (now) period))

(defn from-now
  "Returns a DateTime a supplied period after the present.
  e.g. (-> 30 minutes from-now)"
  [#^Period period]
  (plus (now) period))

(defn interval
  "Returns an interval representing the span between the two given ReadableDateTimes.
   Note that intervals are closed on the left and open on the right."
  [#^ReadableDateTime dt-a #^ReadableDateTime dt-b]
  (Interval. dt-a dt-b))

(defn start
  "Returns the start DateTime of an Interval."
  [#^Interval in]
  (.getStart in))

(defn end
  "Returns the end DateTime of an Interval."
  [#^Interval in]
  (.getEnd in))

(defn extend
  "Returns an Interval with an end ReadableDateTime the specified Period after the end
   of the given Interval"
  [#^Interval in & by]
  (.withEnd in (apply plus (end in) by)))

(defn in-msecs
  "Returns the number of milliseconds in the given Interval."
  [#^Interval in]
  (.toDurationMillis in))

(defn in-secs
  "Returns the number of standard seconds in the given Interval."
  [#^Interval in]
  (.getSeconds (.toPeriod in (secs))))

(defn in-minutes
  "Returns the number of standard minutes in the given Interval."
  [#^Interval in]
  (.getMinutes (.toPeriod in (minutes))))

(defn in-hours
  "Returns the number of standard hours in the given Interval."
  [#^Interval in]
  (.getHours (.toPeriod in (hours))))

(defn in-days
  "Returns the number of standard days in the given Interval."
  [#^Interval in]
  (.getDays (.toPeriod in (days))))

(defn in-weeks
  "Returns the number of standard weeks in the given Interval."
  [#^Interval in]
  (.getWeeks (.toPeriod in (weeks))))

(defn in-months
  "Returns the number of standard years in the given Interval."
  [#^Interval in]
  (.getMonths (.toPeriod in (months))))

(defn in-years
  "Returns the number of standard years in the given Interval."
  [#^Interval in]
  (.getYears (.toPeriod in (years))))

(defn days-between
  "Returns the number of days between two given DateTimes"
  [#^DateTime x #^DateTime y]
  (.getDays (Days/daysBetween x y)))

(defn within?
  "Returns true if the given Interval contains the given ReadableDateTime. Note that
   if the ReadableDateTime is exactly equal to the end of the interval, this function
   returns false."
  [#^Interval i #^ReadableDateTime dt]
  (.contains i dt))

(defn overlaps?
  "Returns true of the two given Intervals overlap. Note that intervals that
   satisfy abuts? do not satisfy overlaps?"
  [#^Interval i-a #^Interval i-b]
  (.overlaps i-a i-b))

(defn abuts?
  "Returns true if Interval i-a abuts i-b, i.e. then end of i-a is exactly the
   beginning of i-b."
  [#^Interval i-a #^Interval i-b]
  (.abuts i-a i-b))

(defn mins-ago [d]
  (in-minutes (interval d (now))))
