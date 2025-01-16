(ns moontv.swnr.shipconfigurator.subs-test
  (:require
   [cljs.spec.test.alpha :as stest]
   [clojure.test.check]
   [clojure.test.check.properties]
   [moontv.swnr.shipconfigurator.subs :as sub]))

(stest/check `sub/ship-data)
(stest/check `sub/selected-ship)
;TODO: actual test task
;(-> (stest/enumerate-namespace 'moontv.swnr.shipconfigurator.subs) stest/check) ;; XXX: this doesn't work, likely because check doesn't support lexical scoping, can i have a macro fix this?
