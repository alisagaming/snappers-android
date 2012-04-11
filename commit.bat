@CALL git commit
@if NOT ERRORLEVEL 0 goto END
@DEL res\raw\commit.txt
@git rev-parse HEAD > res\raw\commit.txt
:END
