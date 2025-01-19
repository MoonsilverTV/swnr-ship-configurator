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

(s/def ::fitting-id keyword?) ;; TODO constrain this or no?

(s/def ::fitting-name (s/and string? not-empty))
(s/def ::fitting-cost-scales? (s/nilable boolean?))
(s/def ::fitting-cost-base (s/or :reasonable-integer (s/int-in 0 (inc (* 1000 1000)))
                                 :special #{:special}))
(s/def ::fitting-cost (s/and (s/keys :req [::fitting-cost-base ::fitting-cost-scales?])
                             #(or (not (::fitting-cost-scales? %))
                                  (pos? (second (::fitting-cost-base %))))))
(s/def ::fitting-power-cost-base (s/int-in -1 5))
(s/def ::fitting-power-scales? ::fitting-cost-scales?)
(s/def ::fitting-power (s/and (s/keys :req [::fitting-power-cost-base ::fitting-power-scales?])
                              #(or (not (::fitting-power-scales? %))
                                   (not= 0 (::fitting-power-cost-base %)))))
(s/def ::fitting-mass-cost-base (s/or :0.5 #{0.5}
                                      :valid-integer (s/int-in -2 5)))
(s/def ::fitting-mass-scales? ::fitting-cost-scales?)
(s/def ::fitting-mass (s/and (s/keys :req [::fitting-mass-cost-base ::fitting-mass-scales?])
                             #(or (not (::fitting-mass-scales? %))
                                  (not= 0 (second (::fitting-mass-cost-base %))))))
(s/def ::fitting-min-class ::ship-class)
(s/def ::fitting-effect-text (s/and string? not-empty))

(s/def ::fitting-data-record (s/keys :req [::fitting-name ::fitting-cost ::fitting-power ::fitting-mass ::fitting-min-class ::fitting-effect-text]))

(s/def ::fitting-data (s/and (s/map-of ::fitting-id ::fitting-data-record)
                             not-empty))

(s/def ::app-db (s/and (s/keys :req [::ship-data ::selected-ship ::fitting-data])
                       ;#(c/contains? (::ship-data %) (::selected-ship %)) TODO: need to adapt generators for this...
                       ))

(comment
  #_{:clj-kondo/ignore [:unresolved-namespace]}
  (re-frame.core/dispatch :moontv.swnr.shipconfigurator.events/initialize)
  (s/explain ::app-db @re-frame.db/app-db)

  (require 'clojure.test.check.generators )
  (s/explain-data ::fitting-cost {:moontv.swnr.shipconfigurator.db-spec/fitting-cost-base
                                  25000
                                  :moontv.swnr.shipconfigurator.db-spec/fitting-cost-scales? true})

  )
