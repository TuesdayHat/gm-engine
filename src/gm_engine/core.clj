(ns gm-engine.core
  (:gen-class))

;; helpers

(defn indexes-of [e coll] (keep-indexed #(if (= e %2) %1) coll))

(defn parse-int [n]
  (Integer. (re-find #"[0-9]+" n)))

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

(defn unique
  "PLACEHOLDER -- returns a number of unique random numbers"
  [input table]
  input
)

(defn roll-keep
  "[col of ints] int bool; check type (high or low), take (num) highest or lowest elements of input collection"
  ([input num] ;roll high
   (roll-keep input num false))
  ([input num low?] 
   (let [sorted (sort input)
         keepType (if low? >= <)
         length (count input)]
     (loop [i (if low? 0 (- length 1)) result []]
       (if (keepType i (if low? num (- length num)))
         result
         (recur (if low? (inc i) (dec i)) 
                (into result [(nth sorted i)])))))))

(defn parser ;;TODO refactor recursion call
  "Main parsing function. recursively resolves parts of input. Takes in a formatted vector, outputs a vector"
  [input]
  (cond
    ;;parentheses handling
    (> (.indexOf input ")") -1) (let [left-par (+ (last (indexes-of "(" input))1) right-par (.indexOf input ")") expand (subvec input left-par right-par)]
                                  (parser 
                                   (into [] 
                                         (concat 
                                          (into [] (subvec input 0 (- left-par 1))) 
                                          (parser expand)
                                          (into [] (subvec input (+ right-par 1)))
                                          )
                                         )
                                   )
                                  );TODO: figure out how to make this all work with lazy seqs
    ;;DICE
    (> (.indexOf input "d") -1) (let [dice (get input (- (.indexOf input "d") 1))
                                      size (get input (+ (.indexOf input "d") 1))
                                      rolls (roll dice size)
                                      next (get input (+ (.indexOf input "d") 2))
                                      after (get input (+ (.indexOf input "d") 3))]
                                 (cond
                                   ;keep low
                                   (and (= next "k") 
                                        (= after "l")) (parser
                                                        (into []
                                                              (concat
                                                               (into [] (subvec input 0 (- (.indexOf input "d") 1))) ;left
                                                               (conj [] (apply + (roll-keep rolls (get input (+ 1 (.indexOf input after))) true))) ;resolve
                                                               (into [] (subvec input (+ (.indexOf input after) 2))) ;right
                                                               )))
                                   ;keep high
                                   (= next "k") (parser 
                                                 (into []
                                                       (concat
                                                        (into [] (subvec input 0 (- (.indexOf input "d") 1)))
                                                        (conj [] (apply + (roll-keep rolls after)))
                                                        (into [] (subvec input (+ 1 (.indexOf input after))))
                                                        )))
                                   ;pool
                                   (= next ">") (parser
                                                 (into []
                                                       (concat
                                                        (into [] (subvec input 0 (- (.indexOf input "d") 1)))
                                                        (conj [] (pool rolls after))
                                                        (into [] (subvec input (+ 1 (.indexOf input after))))
                                                        )))
                                   ;resolve
                                   :else (parser
                                          (into []
                                                (concat
                                                 (into [] (subvec input 0 (- (.indexOf input "d") 1)))
                                                 (conj [] (apply + rolls))
                                                 (into [] (subvec input (+ 2 (.indexOf input "d"))))
                                                 )))
                                   )
                                 )
    ;MATH -- TODO: REFACTOR
    (> (.indexOf input "/") -1) (parser
                                 (into []
                                       (concat
                                        (into [] (subvec input 0 (- (.indexOf input "/") 1)))
                                        (conj [] (int (Math/ceil (/ (get input (- (.indexOf input "/") 1)) (get input(+ (.indexOf input "/") 1))))))
                                        (into [] (subvec input (+ (.indexOf input "/") 2)))
                                        )))

    (> (.indexOf input "*") -1) (parser
                                 (into []
                                       (concat
                                        (into [] (subvec input 0 (- (.indexOf input "*") 1)))
                                        (conj [] (* (get input (- (.indexOf input "*") 1)) (get input(+ (.indexOf input "*") 1))))
                                        (into [] (subvec input (+ (.indexOf input "*") 2)))
                                        )))

    (> (.indexOf input "+") -1) (parser
                                 (into []
                                       (concat
                                        (into [] (subvec input 0 (- (.indexOf input "+") 1)))
                                        (conj [] (+ (get input (- (.indexOf input "+") 1)) (get input(+ (.indexOf input "+") 1))))
                                        (into [] (subvec input (+ (.indexOf input "+") 2)))
                                        )))

    (> (.indexOf input "-") -1) (parser
                                 (into []
                                       (concat
                                        (into [] (subvec input 0 (- (.indexOf input "-") 1)))
                                        (conj [] (- (get input (- (.indexOf input "-") 1)) (get input(+ (.indexOf input "-") 1))))
                                        (into [] (subvec input (+ (.indexOf input "-") 2)))
                                        )))
    
    :else input))

(defn formatting
  "breaks input string into a vector of numbers and commands"
  [string]
  (let [comm (re-find #"(?:(?!\#).)*" string)]
    (into [] (map #(if (re-find #"[0-9]+" %) (parse-int %) %))
          (filter #(not= % "") (clojure.string/split 
                                 (clojure.string/replace comm #"([^0-9]|[0-9]+)" (str "$1 ")) #" ")))
    ))

(defn -main
  "wrapper for parser function; handles comments. Takes a string as an argument, returns a string."
  [input]
  (let [chunk-form (formatting input) result (conj (parser chunk-form) (re-find #"\#.+" input))]
    (println result) ;;result in the let field so -main can later be used for formatting shenanigans
    result)
  )

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
