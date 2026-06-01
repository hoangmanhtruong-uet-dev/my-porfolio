# Đường dẫn tới file .env ở thư mục gốc
$envFilePath = "$PSScriptRoot\.env"

if (Test-Path $envFilePath) {
    Write-Host "Setting environment variables from .env file..." -ForegroundColor Cyan
    Get-Content $envFilePath | Where-Object { $_ -and -not $_.StartsWith("#") } | ForEach-Object {
        $name, $value = $_ -split '=', 2
        if ($name -and $value) {
            [System.Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim(), "Process")
        }
    }
} else {
    Write-Host "Warning: .env file not found. Make sure environment variables are configured on your system!" -ForegroundColor Yellow
}

# Chạy Spring Boot backend (pom.xml nằm trong thư mục backend)
Set-Location $PSScriptRoot\backend
mvn spring-boot:run
