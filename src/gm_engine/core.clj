(ns gm-engine.core
  (:gen-class
   :name engine))

(defn -main
  "primary gm engine function -- takes in a vector of strings, calls relevant functions"
  [input]
  (let [chunk-form (chunker input)]
    (conj (parser chunk-form) (re-find #"\#.+" input) ))
 )

(def str-one "10d6+3d6+10 #hello world")
(def str-two "2d6+((3d4+4)/2)#dmg")

;; (Defn Parser
;;   [chunks]
;;   (let [[a comm b & remain] chunks]
;;     (printf "a: %s ; b: %s ; comm: %s ; remain: %s" a b comm remain)
;;     ((get comm-list comm) (parse-int a) (parse-int b)))
;; )

(defn parser
  [input]
  (cond
    (> (.indexOf input ")") -1) (let [left-par (+ (last (indexes-of "(" input))1) ;parentheses handling
                                      right-par (.indexOf input ")")
                                      expand (subvec input left-par right-par)]
                                  ;(println expand)
                                  ;(println (subvec input 0 (- left-par 1)))
                                  (parser (into [] 
                                                (concat 
                                                 (into [] (subvec input 0 (- left-par 1))) 
                                                 (parser expand) 
                                                 (into [] (subvec input (+ right-par 1)))))));TODO: figure out how to make this all work with lazy seqs
    
    :else input))


(defn test-recursion 
  [x]
  (if (> x 5)
    x
    (test-recursion (inc x))))

(defn chunker
  "breaks input string into a vector of numbers and commands"
  [string]
  (let [comm (re-find #"(?:(?!\#).)*" string)]
    (clojure.string/split (clojure.string/replace comm #"([^0-9]|[0-9]+)" (str "$1 ")) #" ")
    )
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

(defn roll-keep
  "[col of ints] int bool; check type (high or low), take (num) highest or lowest elements of input collection"
  ([input num]
   (roll-keep input num false))
  ([input num low?]  
   (let [sorted (sort input)
         keepType (if low? >= <=)
         length (count input)]
     (loop [i (if low? 0 (- length 1)) result []]
       (if (keepType i (if low? num (- length num)))
         result
         (recur (if low? (inc i) (dec i)) 
                (into result [(nth sorted i)])))))))


;; helpers

(defn indexes-of [e coll] (keep-indexed #(if (= e %2) %1) coll))

(defn parse-int [n]
  (Integer. (re-find #"[0-9]+" n)))

;; Variables

(def operations ["(" "d" ">" "*" "/" "+"])

(def comm-list {"d" roll
                "=" total
                ">" pool
                "k" roll-keep ;TODO a way to detect keep high or low
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
