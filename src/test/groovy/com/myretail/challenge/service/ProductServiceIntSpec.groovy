package com.myretail.challenge.service

import com.myretail.challenge.models.Price
import com.myretail.challenge.models.Product
import com.myretail.challenge.repositories.PriceRepo
import org.apache.commons.io.Charsets
import org.apache.http.HttpEntity
import org.apache.http.ProtocolVersion
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.message.BasicStatusLine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
@SuppressWarnings("GroovyAccessibility")
class ProductServiceIntSpec extends Specification {

    @Autowired
    ProductService productService

    PriceRepo mockPriceRepo = Mock(PriceRepo)
    CloseableHttpClient mockHttpClient = Mock(CloseableHttpClient)

    ProductService mockService = new ProductService(mockPriceRepo, mockHttpClient)

    def "getProduct"() {
        given:
        def productId = 13860428

        when:
        def result = productService.getProduct(productId)

        then:
        result.getName() == 'The Big Lebowski (Blu-ray)'
    }

    def "test unable to find product from external call"() {
        given:
        def productId = 12541515

        when:
        productService.getProduct(productId)

        then:
        def e = thrown(ProductNotFoundException)
        e.getMessage().contains(String.valueOf(productId))
    }

    def "test short circuit of update when there is nothing to update"() {
        given:
        def productId = 12345
        def prices = [new Price(UUID.randomUUID(), 12.1, "USD", productId)] as Set
        def product = new Product(productId, 'The Big Lebowski (Blu-ray)', prices)

        CloseableHttpResponse mockResponse = Mock(CloseableHttpResponse)
        HttpEntity mockEntity = Mock(HttpEntity)

        when:
        def result = mockService.updateProduct(productId, product)

        then:
        1 * mockResponse.getStatusLine() >> new BasicStatusLine(new ProtocolVersion("http", 1, 1), 200, "")
        1 * mockEntity.getContent() >> new ByteArrayInputStream(json.getBytes(Charsets.UTF_8))
        1 * mockResponse.getEntity() >> mockEntity
        1 * mockHttpClient.execute(_ as HttpGet) >> mockResponse
        1 * mockPriceRepo.findByProductId(productId) >> prices
        result == product
    }


    def "test price productId not matching provided productId"() {
        given:
        def productId = 12345
        def product = new Product(productId, "test", [new Price(UUID.randomUUID(), 12.1, "USD", 54321)] as Set)

        when:
        productService.validatePriceProductId(productId, product)

        then:
        def e = thrown(InvalidUpdateProductRequest)
        e.getMessage().contains('Attempted update of Price with none matching ProductIds.')
    }

    def "test matching price and productId during validation of update"() {
        given:
        def productId = 12345
        def product = new Product(productId, "test", [new Price(UUID.randomUUID(), 12.1, "USD", productId)] as Set)

        expect:
        productService.validatePriceProductId(productId, product)
    }

    def "test parsing RedSky response"() {
        when:
        def response = productService.parseResponseForName(json)
        then:
        response == 'The Big Lebowski (Blu-ray)'
    }

    def "test failing to parse json response"() {
        given:
        def badJson = '''{ "prodct": {} }'''

        when:
        productService.parseResponseForName(badJson)

        then:
        def e = thrown(ResponseParseException)
        e.getMessage().contains('Unable to parse response: ')
    }


    def json = '''{
  "product": {
    "available_to_promise_network": {
      "product_id": "13860428",
      "id_type": "TCIN",
      "available_to_promise_quantity": 0.0,
      "street_date": "2011-11-15T06:00:00.000Z",
      "availability": "UNAVAILABLE",
      "online_available_to_promise_quantity": 0.0,
      "stores_available_to_promise_quantity": 0.0,
      "availability_status": "OUT_OF_STOCK",
      "multichannel_options": [],
      "is_infinite_inventory": false,
      "loyalty_availability_status": "OUT_OF_STOCK",
      "loyalty_purchase_start_date_time": "1970-01-01T00:00:00.000Z",
      "is_loyalty_purchase_enabled": false,
      "is_out_of_stock_in_all_store_locations": false,
      "is_out_of_stock_in_all_online_locations": true
    },
    "item": {
      "tcin": "13860428",
      "bundle_components": {},
      "dpci": "058-34-0436",
      "upc": "025192110306",
      "product_description": {
        "title": "The Big Lebowski (Blu-ray)",
        "downstream_description": "Jeff \\"The Dude\\" Lebowski (Bridges) is the victim of mistaken identity. Thugs break into his apartment in the errant belief that they are accosting Jeff Lebowski, the eccentric millionaire philanthropist, not the laid-back, unemployed Jeff Lebowski. In the aftermath, \\"The Dude\\" seeks restitution from his wealthy namesake. He and his buddies (Goodman and Buscemi) are swept up in a kidnapping plot that quickly spins out of control.",
        "bullet_description": [
          "<B>Movie Studio:</B> Universal Studios",
          "<B>Movie Genre:</B> Comedy",
          "<B>Run Time (minutes):</B> 119",
          "<B>Software Format:</B> Blu-ray"
        ]
      },
      "buy_url": "https://www.target.com/p/the-big-lebowski-blu-ray/-/A-13860428",
      "enrichment": {
        "images": [
          {
            "base_url": "https://target.scene7.com/is/image/Target/",
            "primary": "GUEST_44aeda52-8c28-4090-85f1-aef7307ee20e",
            "content_labels": [
              {
                "image_url": "GUEST_44aeda52-8c28-4090-85f1-aef7307ee20e"
              }
            ]
          }
        ],
        "sales_classification_nodes": [
          {
            "node_id": "hp0vg"
          },
          {
            "node_id": "5xswx"
          }
        ]
      },
      "return_method": "Temporary return policy: For a limited time, returns are not accepted in store. Return windows will be extended to accommodate this change. Standard return policy: This item can be returned to any Target store or Target.com.",
      "handling": {},
      "recall_compliance": {
        "is_product_recalled": false
      },
      "tax_category": {
        "tax_class": "G",
        "tax_code_id": 99999,
        "tax_code": "99999"
      },
      "display_option": {
        "is_size_chart": false
      },
      "fulfillment": {
        "is_po_box_prohibited": true,
        "po_box_prohibited_message": "We regret that this item cannot be shipped to PO Boxes.",
        "box_percent_filled_by_volume": 0.27,
        "box_percent_filled_by_weight": 0.43,
        "box_percent_filled_display": 0.43
      },
      "package_dimensions": {
        "weight": "0.18",
        "weight_unit_of_measure": "POUND",
        "width": "5.33",
        "depth": "6.65",
        "height": "0.46",
        "dimension_unit_of_measure": "INCH"
      },
      "environmental_segmentation": {
        "is_hazardous_material": false,
        "has_lead_disclosure": false
      },
      "manufacturer": {},
      "product_vendors": [
        {
          "id": "1984811",
          "manufacturer_style": "025192110306",
          "vendor_name": "Ingram Entertainment"
        },
        {
          "id": "4667999",
          "manufacturer_style": "61119422",
          "vendor_name": "UNIVERSAL HOME VIDEO"
        },
        {
          "id": "1979650",
          "manufacturer_style": "61119422",
          "vendor_name": "Universal Home Ent PFS"
        }
      ],
      "product_classification": {
        "product_type": "542",
        "product_type_name": "ELECTRONICS",
        "item_type_name": "Movies",
        "item_type": {
          "category_type": "Item Type: MMBV",
          "type": 300752,
          "name": "movies"
        }
      },
      "product_brand": {
        "brand": "Universal Home Video",
        "manufacturer_brand": "Universal Home Video",
        "facet_id": "55zki"
      },
      "item_state": "READY_FOR_LAUNCH",
      "specifications": [],
      "attributes": {
        "gift_wrapable": "Y",
        "has_prop65": "N",
        "is_hazmat": "N",
        "manufacturing_brand": "Universal Home Video",
        "max_order_qty": 10,
        "street_date": "2011-11-15",
        "media_format": "Blu-ray",
        "merch_class": "MOVIES",
        "merch_classid": 58,
        "merch_subclass": 34,
        "return_method": "Temporary return policy: For a limited time, returns are not accepted in store. Return windows will be extended to accommodate this change. Standard return policy: This item can be returned to any Target store or Target.com.",
        "ship_to_restriction": "United States Minor Outlying Islands,American Samoa (see also separate entry under AS),Puerto Rico (see also separate entry under PR),Northern Mariana Islands,Virgin Islands, U.S.,APO/FPO,Guam (see also separate entry under GU)"
      },
      "country_of_origin": "US",
      "relationship_type_code": "Stand Alone",
      "subscription_eligible": false,
      "ribbons": [],
      "tags": [],
      "ship_to_restriction": "This item cannot be shipped to the following locations: United States Minor Outlying Islands, American Samoa, Puerto Rico, Northern Mariana Islands, Virgin Islands, U.S., APO/FPO, Guam",
      "estore_item_status_code": "A",
      "is_proposition_65": false,
      "return_policies": {
        "user": "Regular Guest",
        "policyDays": "30",
        "guestMessage": "This item must be returned within 30 days of the in-store purchase, ship date, or online order pickup. See return policy for details."
      },
      "gifting_enabled": false,
      "packaging": {
        "is_retail_ticketed": false
      }
    },
    "circle_offers": {
      "universal_offer_exists": false,
      "non_universal_offer_exists": true
    }
  }
}'''
}
