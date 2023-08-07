resource "kubernetes_config_map" "keycloak-realm" {
  metadata {
    name = "keycloak-realm"
  }
  data = {
    "miw_test_realm.json" = file("./keycloak/miw_test_realm.json")
  }
}