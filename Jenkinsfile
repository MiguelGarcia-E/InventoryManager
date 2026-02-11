pipeline {
  agent none

  environment {
    COMPOSE_PROJECT_NAME = "inventorymanager-ci"
  }

  stages {

    stage('Build Backend (Unit tests)') {
      agent {
        docker { image 'maven:3.9-eclipse-temurin-17' }
      }
      steps {
        dir('back-end-inventory-manager') {
          sh 'mvn -B test'
        }
      }
      post {
        always {
          dir('back-end-inventory-manager') {
            junit '**/target/surefire-reports/*.xml'
          }
        }
      }
    }

    stage('Build Frontend (Vite build)') {
      agent {
        docker { image 'node:20-alpine' }
      }
      steps {
        dir('Inventory-Manager') {
          sh 'node -v'
          sh 'npm -v'
          sh 'npm ci'
          sh 'npm run build'
        }
      }
    }

    stage('Build Docker Images + Compose Up') {
      agent any
      steps {
        sh '''
          docker compose -p "$COMPOSE_PROJECT_NAME" down -v || true
          docker compose -p "$COMPOSE_PROJECT_NAME" build --no-cache
          docker compose -p "$COMPOSE_PROJECT_NAME" up -d
        '''
      }
    }

    stage('Wait Backend Healthy + Smoke Tests') {
      agent any
      steps {
        sh '''
          echo "== Wait for backend container HEALTHY =="
          for i in $(seq 1 60); do
            STATUS=$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}no-healthcheck{{end}}' inventory-backend 2>/dev/null || true)
            echo "backend health: $STATUS"
            if [ "$STATUS" = "healthy" ]; then
              break
            fi
            sleep 2
          done

          if [ "$STATUS" != "healthy" ]; then
            echo "Backend did not become healthy"
            docker logs --tail=200 inventory-backend || true
            exit 1
          fi

          echo "== Readiness endpoint =="
          curl -fsS http://localhost:9090/actuator/health/readiness | tee /tmp/readiness.json
          grep -q '"status":"UP"' /tmp/readiness.json

          echo "== Front served by Nginx =="
          curl -fsS -I http://localhost:8080 | head -n 20
        '''
      }
    }
  }

  post {
    always {
      sh '''
        docker compose -p "$COMPOSE_PROJECT_NAME" ps || true
        docker compose -p "$COMPOSE_PROJECT_NAME" logs --no-color --tail=200 || true
        docker compose -p "$COMPOSE_PROJECT_NAME" down -v || true
      '''
    }
  }
}
