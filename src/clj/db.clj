(ns db
  (:require [next.jdbc :as jdbc]
            [clojure.string :as s]))

(def db-spec {:jdbcUrl   "jdbc:sqlite:data/database.db"})

(def schema-user-table ["create table if not exists users (
    id integer primary key,
    first_name text not null
)"])

(def insert-initial-users-statement ["insert into users (first_name) values
  ('Jerry'),
  ('Jenny'),
  ('George'),
  ('Johanna'),
  ('John'),
  ('Anne')"])

(defn create-table [table-schema db-spec]
  (try
    (jdbc/execute! db-spec  table-schema)
    (catch Exception e
      (println (.getMessage e)))))


(defn seed-db []
  (with-open [connection (jdbc/get-connection db-spec)]
    (jdbc/execute! connection  insert-initial-users-statement)))


(defn snake-case->kebab-case
  [column]
  (when (keyword? column)
    (keyword (s/replace (name column) #"_" "-"))))

(defn format-output-keywords
  "Convert `output` keywords from snake_case to kebab-case."
  [output]
  (reduce-kv (fn [m k v]
               (assoc m (snake-case->kebab-case k) v))
             {}
             output))

(defn ^:dynamic fetch-user [id]
  (with-open
   [connection (jdbc/get-connection db-spec)]
    (println connection)
    (->>
     (let [result (jdbc/execute-one! connection  ["select * from users where id= ? " id])]
       (if (nil? result) nil
           (format-output-keywords result))))))


(comment
  (create-table schema-user-table db-spec)
  (seed-db))