(ns gm-engine.core
  (:gen-class))

(defn -main
  "primary gm engine function -- takes in a vector of strings, calls relevant functions"
  [& args]
  (println "Hello, World!"))

(defn chunker
  "takes in a string, creates a vector of each command"
  [string]
  )

(defn roll
  [dice size]
  (mapv (fn [x] (+ 1 (rand-int size))) (range dice)))

(defn rollLoop
  "2 args: number of rolls, size of dice"
  [rolls size]
  (loop [i rolls result []]
    (if (zero? i)
      result
      (do
        (recur (dec i) (into result
                             [(+ 1 (rand-int size))]))))))

(defn total
  "takes a vector of integers, outputs the original vector and the total of all members."
  [input]
  (str input " total: " (apply + input)))

(defn pool
  [input limit]
  (apply + (map #(if (>= % limit) 1 0) input))
)

(defn rollKeep
  "[col of ints] int bool; check type (high or low), take (num) highest or lowest elements of input collection"
  ([input num]
   (rollKeep input num false))
  ([input num low?]  
   (let [sorted (sort input)
         keepType (if low? >= <=)
         length (count input)]
     (loop [i (if low? 0 (- length 1)) result []]
       (if (keepType i (if low? num (- length num)))
         result
         (recur (if low? (inc i) (dec i)) 
                (into result [(nth sorted i)])))))))

