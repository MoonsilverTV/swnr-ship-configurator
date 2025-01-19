(ns moontv.swnr.shipconfigurator.sub
  (:require
   [clojure.spec.alpha :as s]
   [moontv.swnr.shipconfigurator.db-spec :as db-s]
   [re-frame.core :as rf]))

;; XXX: monitor this, this does seem like a strong macro candidate (if specs stay in the same file)

(s/def ::ship-data ::db-s/ship-data)
(s/fdef ship-data
  :args (s/cat :db ::db-s/app-db :_ any?)
  :ret ::ship-data)
(defn ship-data [db _]
  (::db-s/ship-data db))
(rf/reg-sub
 ::ship-data
 ship-data)

(s/def ::selected-ship ::db-s/selected-ship)
(s/fdef selected-ship
  :args (s/cat :db ::db-s/app-db :_ any?)
  :ret ::selected-ship)
(defn selected-ship [db _]
  (::db-s/selected-ship db))
(rf/reg-sub
 ::selected-ship
 selected-ship)