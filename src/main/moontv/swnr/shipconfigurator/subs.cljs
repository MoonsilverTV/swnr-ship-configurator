(ns moontv.swnr.shipconfigurator.subs
  (:require
   [moontv.swnr.shipconfigurator.db-spec :as db-s]
   [re-frame.core :as rf]))

; TODO: subscription specs (possibly in another file, check best practices)

(rf/reg-sub
 ::ship-data
 (fn [db _]
   (::db-s/ship-data db)))

(rf/reg-sub
 ::selected-ship
 (fn [db _]
   (::db-s/selected-ship db)))
