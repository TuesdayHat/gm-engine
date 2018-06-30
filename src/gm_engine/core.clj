(ns gm-engine.core
  (:gen-class))

(defn -main
  "primary gm engine function -- takes in a vector of strings, calls relevant functions"
  [& args]
  (println "Hello, World!"))

(def test-str "10000d20+3d6+10 #hello world")

(defn chunker
  [string]
  (let [comm (re-find #"(?:(?!\#).)*" string)]
    (subvec (clojure.string/split (clojure.string/replace comm #"([0-9]+)" (str " $1 ")) #" ")1))
)

(defn roll
  "roll (dice) d (size)"
  [dice size]
  (mapv (fn [x] (+ 1 (rand-int size))) (range dice)))

(defn total
  "takes a vector of integers, outputs the original vector and the total of all members."
  [input]
  (str input " total: " (apply + input)))

(defn pool
  "returns the number of dice which roll above limit"
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


;; Variables

(def comm-list {"d" roll
                "=" total
                ">" pool
                "k" rollKeep
                "+" +
                "*" *
                "/" /
                 })

;; Science Experiments

(defn rollLoop
  "2 args: number of rolls, size of dice"
  [rolls size]
  (loop [i rolls result []]
    (if (zero? i)
      result
      (do
        (recur (dec i) (into result
                             [(+ 1 (rand-int size))]))))))
