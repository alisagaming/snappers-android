@CALL git commit
@if NOT ERRORLEVEL 0 goto END
@git rev-parse HEAD > res/raw/commit.txt
:END
