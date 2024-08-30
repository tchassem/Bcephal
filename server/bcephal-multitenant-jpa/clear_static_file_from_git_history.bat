@echo off

git filter-branch --index-filter "git rm -rf --cached --ignore-unmatch UpdateDa77247774dReportLinked.sql" HEAD
git push --force