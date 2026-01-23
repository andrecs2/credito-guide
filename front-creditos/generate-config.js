const fs = require('fs');
const path = require('path');

// Carregar .env do diretório raiz do projeto
const envPath = path.join(__dirname, '..', '.env');
if (fs.existsSync(envPath)) {
  require('dotenv').config({ path: envPath });
} else {
  require('dotenv').config();
}

// Determinar ambiente
const isDocker = process.env.ENVIRONMENT === 'docker' || 
                 fs.existsSync('/.dockerenv') || 
                 process.env.DOCKER === 'true';

const config = {
  production: false,
  apiUrl: isDocker ? '/api' : (process.env.BACKEND_URL || 'http://localhost:8080'),
  apiVersion: process.env.API_VERSION || 'v1',
  appName: process.env.APP_NAME || 'Crédito Guide',
  environment: isDocker ? 'docker' : (process.env.ENVIRONMENT || 'development'),
  features: {
    enableKafka: process.env.KAFKA_ENABLED === 'true',
    enableAnalytics: false
  },
  // Configurações Docker específicas
  docker: {
    host: process.env.HOST || '0.0.0.0',
    port: process.env.FRONTEND_PORT || 4200
  }
};

// Criar environment.ts
const envContent = `// Auto-generated - Do not edit manually
export const environment = ${JSON.stringify(config, null, 2)};
`;

// Criar environment.docker.ts
const dockerEnvContent = `// Docker environment
export const environment = ${JSON.stringify({
  ...config,
  apiUrl: '/api',
  environment: 'docker'
}, null, 2)};
`;

// Escrever arquivos
fs.writeFileSync(
  path.join(__dirname, 'src/environments/environment.ts'),
  envContent
);

fs.writeFileSync(
  path.join(__dirname, 'src/environments/environment.docker.ts'),
  dockerEnvContent
);

console.log('✅ Configurações geradas:');
console.log(`   Ambiente: ${config.environment}`);
console.log(`   API URL: ${config.apiUrl}`);
console.log(`   Modo Docker: ${isDocker}`);