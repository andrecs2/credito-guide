const fs = require('fs');
const path = require('path');

require('dotenv').config({ path: path.resolve(__dirname, '../.env') });

const templatePath = path.join(__dirname, 'public/config.template.json');
const outputPath = path.join(__dirname, 'public/config.json');


if (!fs.existsSync(templatePath)) {
    console.error('❌ Erro: public/config.template.json não encontrado!');
    process.exit(1);
}

let content = fs.readFileSync(templatePath, 'utf8');


const replacedContent = content.replace(/\${(\w+)}/g, (match, key) => {
    return process.env[key] || match; 
});

fs.writeFileSync(outputPath, replacedContent);
console.log('✅ Local config.json gerado a partir do .env superior.');