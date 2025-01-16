(ns moontv.swnr.shipconfigurator.db-spec
  (:require [clojure.spec.alpha :as s]
            [re-frame.db :as db]
            [cljs.core :as c]))

(def pos-or-zero (s/or ::positive pos? ::zero zero?))

(s/def ::ship-id keyword?) ;; TODO constrain this or no? ...
(s/def ::selected-ship ::ship-id)
(s/def ::ship-name (s/and string? not-empty))
(s/def ::ship-cost (s/and number? pos-or-zero))
(s/def ::ship-speed (s/nilable (s/and int? pos-or-zero)))
(s/def ::ship-armor (s/int-in 0 51))
(s/def ::ship-hp (s/int-in 0 1000))
(s/def ::ship-crew-min (s/int-in 1 10000))
(s/def ::ship-crew-max (s/int-in 1 10000))
(s/def ::ship-ac (s/int-in 1 36))
(s/def ::ship-power (s/int-in 1 1000))
(s/def ::ship-mass (s/int-in 1 1000))
(s/def ::ship-hardpoints (s/int-in 1 100))
(s/def ::ship-class #{::fighter ::frigate ::cruiser ::capital})

(s/def ::ship-data-record (s/and
                           (s/keys :req [::ship-name ::ship-cost ::ship-speed
                                         ::ship-armor ::ship-hp ::ship-crew-min
                                         ::ship-crew-max ::ship-ac ::ship-power
                                         ::ship-mass ::ship-hardpoints ::ship-class])
                           #(<= (::ship-crew-min %) (::ship-crew-max %))))

(s/def ::ship-data (s/and (s/map-of ::ship-id ::ship-data-record)
                          #(not-empty %)))

(s/def ::app-db (s/keys :req [::ship-data ::selected-ship]))

(comment
  #_{:clj-kondo/ignore [:unresolved-namespace]}
  (re-frame.core/dispatch :moontv.swnr.shipconfigurator.events/initialize)
  (s/explain ::app-db @re-frame.db/app-db))

