using System;
using System.ComponentModel;
using acme_order.Auth;
using acme_order.Configuration;
using acme_order.Db;
using acme_order.Services;
using Azure.Extensions.AspNetCore.Configuration.Secrets;
using Azure.Identity;
using Azure.Security.KeyVault.Secrets;
using Microsoft.ApplicationInsights.Extensibility;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Options;
using Steeltoe.Management.Endpoint;
var builder = WebApplication.CreateBuilder(args);
var services = builder.Services;
var configuration = builder.Configuration;
builder.AddAllActuators();
var keyVaultUri = configuration["ConnectionStrings:KeyVaultUri"];
if (!string.IsNullOrEmpty(keyVaultUri))
{
    var secretClient = new SecretClient(
        new Uri(keyVaultUri),
        new DefaultAzureCredential());
    configuration.AddAzureKeyVault(secretClient, new KeyVaultSecretManager());
}


services.Configure<AcmeServiceSettings>(configuration.GetSection(nameof(AcmeServiceSettings)));

services.AddSingleton<IAcmeServiceSettings>(sp =>
    sp.GetRequiredService<IOptions<AcmeServiceSettings>>().Value);

switch (configuration["DatabaseProvider"])
{
    case "Sqlite":
        services.AddDbContext<OrderContext, SqliteOrderContext>(ServiceLifetime.Singleton);
        break;

    case "Postgres":
        services.AddDbContext<OrderContext, PostgresOrderContext>(ServiceLifetime.Singleton);
        break;
}

services.AddSingleton<OrderService>();
services.AddControllers();
services.AddScoped<AuthorizeResource>();

services.AddApplicationInsightsTelemetry();
services.AddSingleton<ITelemetryInitializer, CloudRoleNameTelemetryInitializer>();

var app = builder.Build();
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<OrderContext>();
    db.Database.Migrate();
}
if (app.Environment.IsDevelopment())
{
    app.UseDeveloperExceptionPage();
}

app.UseHttpsRedirection();
app.UseRouting();
app.UseEndpoints(endpoints => endpoints.MapControllers());
await app.RunAsync();