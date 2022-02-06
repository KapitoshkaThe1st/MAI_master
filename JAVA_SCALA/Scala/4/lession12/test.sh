java -cp target/lession12-1.0.1-SNAPSHOT.jar org.mai.dep210.scala.stackoverflow.Main init --force
java -cp target/lession12-1.0.1-SNAPSHOT.jar org.mai.dep210.scala.stackoverflow.Main clean --drop_tables
java -cp target/lession12-1.0.1-SNAPSHOT.jar org.mai.dep210.scala.stackoverflow.Main load --path stackoverflow/
java -cp target/lession12-1.0.1-SNAPSHOT.jar org.mai.dep210.scala.stackoverflow.Main extract --query "select display_name, reputation, creation_date from users where views > 100" --file query_result.csv