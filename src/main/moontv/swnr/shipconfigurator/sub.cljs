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
(defn ship-data [db _query-v]
  (::db-s/ship-data db))
(rf/reg-sub
 ::ship-data
 ship-data)

(s/def ::selected-ship ::db-s/selected-ship)
(s/fdef selected-ship
  :args (s/cat :db ::db-s/app-db :_ any?)
  :ret ::selected-ship)
(defn selected-ship [db _query-v]
  (::db-s/selected-ship db))
(rf/reg-sub
 ::selected-ship
 selected-ship)

(s/def ::fitting-data ::db-s/fitting-data)
(s/fdef fitting-data
  :args (s/cat :db ::db-s/app-db :_ any?)
  :ret ::fitting-data)
(defn fitting-data [db _query-v]
  (::db-s/fitting-data db))
(rf/reg-sub
 ::fitting-data
 fitting-data)

(def ^:private class->price-multiplier {::db-s/fighter 1
                                        ::db-s/frigate 10
                                        ::db-s/cruiser 25
                                        ::db-s/capital 100})
(def ^:private class->power&mass-multiplier {::db-s/fighter 1
                                             ::db-s/frigate 2
                                             ::db-s/cruiser 3
                                             ::db-s/capital 4})
(s/def ::fitting-name (s/and string? not-empty))
(s/def ::fitting-cost (s/or :int int? :special #{"Special"}))
(s/def ::fitting-power int?)
(s/def ::fitting-mass number?)
(s/def ::fitting-class (s/and string? not-empty))
(s/def ::fitting-effect (s/and string? not-empty))
(s/def ::materialized-fitting-options
  (s/coll-of (s/keys :req [::db-s/fitting-id ::fitting-name ::fitting-cost ::fitting-power ::fitting-mass ::fitting-class ::fitting-effect])))
(s/fdef materialized-fitting-options
  :args (s/cat :inputs (s/tuple ::fitting-data ::selected-ship ::ship-data) :_query-v any?)
  :ret ::materialized-fitting-options)
(defn materialized-fitting-options [[fitting-data selected-ship ship-data] _query-v]
  (let [ship (get ship-data selected-ship)
        ship-class (::db-s/ship-class ship)
        price-multiplier  (class->price-multiplier ship-class)
        power&mass-multiplier (class->power&mass-multiplier ship-class)
        fitting-cost-of (fn [fitting] (if (get-in fitting [::db-s/fitting-cost ::db-s/fitting-cost-scales?])
                                        (* price-multiplier (get-in fitting [::db-s/fitting-cost ::db-s/fitting-cost-base]))
                                        (get-in fitting [::db-s/fitting-cost ::db-s/fitting-cost-base])))
        power-cost-of (fn [fitting] (if (get-in fitting [::db-s/fitting-power ::db-s/fitting-power-scales?])
                                      (* power&mass-multiplier (get-in fitting [::db-s/fitting-power ::db-s/fitting-power-cost-base]))
                                      (get-in fitting [::db-s/fitting-power ::db-s/fitting-power-cost-base])))
        mass-cost-of (fn [fitting] (if (get-in fitting [::db-s/fitting-mass ::db-s/fitting-mass-scales?])
                                     (* power&mass-multiplier (get-in fitting [::db-s/fitting-mass ::db-s/fitting-mass-cost-base]))
                                     (get-in fitting [::db-s/fitting-mass ::db-s/fitting-mass-cost-base])))]
    (for [[fitting-id fitting] fitting-data]
      {::db-s/fitting-id fitting-id
       ::fitting-name (::db-s/fitting-name fitting)
       ::fitting-cost (let [cost (fitting-cost-of fitting)]
                        (if (= cost :special)
                          "Special"
                          cost))
       ::fitting-power (power-cost-of fitting)
       ::fitting-mass (mass-cost-of fitting)
       ::fitting-class (str (name (::db-s/fitting-min-class fitting))) ;; TODO properly convert this 
       ::fitting-effect (::db-s/fitting-effect-text fitting)})))

(rf/reg-sub
 ::materialized-fitting-options
 :<- [::fitting-data]
 :<- [::selected-ship]
 :<- [::ship-data]
 materialized-fitting-options)

(comment
  (require 'clojure.spec.gen.alpha))
