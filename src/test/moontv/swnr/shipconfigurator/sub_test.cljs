(ns moontv.swnr.shipconfigurator.sub-test
  (:require
   [cljs.test :refer (deftest is)]
   [cljs.spec.test.alpha :as stest]
   [clojure.test.check]
   [clojure.test.check.properties]
   [moontv.swnr.shipconfigurator.sub]))

(deftest specs
  (->> (stest/check 'moontv.swnr.shipconfigurator.sub
                    {:clojure.spec.test.check/opts {:num-tests 100}})
       (map #(is (= true (get-in % [:clojure.spec.test.check/ret :pass?]))
                 (str "Spec error for: " (:sym %) " info:\n" %)))
       doall))

;; TODO: make an appropriate test abstraction for this