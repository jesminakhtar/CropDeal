$env:RAZORPAY_API_KEY =
    [Environment]::GetEnvironmentVariable(
        "RAZORPAY_API_KEY",
        "User"
    )

$env:RAZORPAY_API_SECRET =
    [Environment]::GetEnvironmentVariable(
        "RAZORPAY_API_SECRET",
        "User"
    )

if ([string]::IsNullOrWhiteSpace($env:RAZORPAY_API_KEY)) {
    throw "RAZORPAY_API_KEY is not configured in Windows User environment variables."
}

if ([string]::IsNullOrWhiteSpace($env:RAZORPAY_API_SECRET)) {
    throw "RAZORPAY_API_SECRET is not configured in Windows User environment variables."
}

Write-Host "Razorpay key loaded: $($env:RAZORPAY_API_KEY.StartsWith('rzp_test_'))"
Write-Host "Razorpay secret loaded: $(-not [string]::IsNullOrWhiteSpace($env:RAZORPAY_API_SECRET))"

.\mvnw.cmd spring-boot:run